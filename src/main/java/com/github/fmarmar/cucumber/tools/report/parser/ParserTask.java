package com.github.fmarmar.cucumber.tools.report.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fmarmar.cucumber.tools.report.model.Feature;
import com.github.fmarmar.cucumber.tools.report.parser.json.util.PathRequestPayload;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ParserTask {

	public static final String METADATA_FILENAME = ".metadata";

	private static final TypeReference<List<Feature>> FEATURES_TYPE_REF = new TypeReference<List<Feature>>() {};

	private static final JsonNode DEFAULT_METADATA = NullNode.getInstance();

	private static final String FEATURE_URI_FIELD_NAME = "uri";

	private static final String ELEMENTS_FIELD_NAME = "elements";

	private static final String METADATA_FIELD_NAME = "metadata";

	private final ObjectMapper mapper;

	private final boolean debugMode;

	private ArrayNode reports = null;

	private Map<String, JsonNode> reportsIndex;

	private JsonNode currentMetadata = DEFAULT_METADATA;

	private Path currentMetadataPath = null;

	public ParserTask(ObjectMapper mapper) {
		this(mapper, false);
	}

	public ParserTask(ObjectMapper mapper, boolean debugMode) {
		this.debugMode = debugMode;
		this.mapper = mapper;
	}

	public List<Feature> run(Path... paths) throws IOException {

		for (Path path : paths) {
			parseOne(path);
		}

		return mapper.convertValue(reports, FEATURES_TYPE_REF);
	}

	private void parseOne(Path path) throws IOException {

		File file = path.toFile();

		if (file.exists()) {

			if (file.isDirectory()) {
				scanDirectory(path);
			}

			if (isJsonFile(path)) {
				parseReport(path);
			}

		}

	}

	private void scanDirectory(Path dir) throws IOException {

		Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

				if (currentMetadata.isNull()) {
					parseMetadata(dir);
				}

				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

				super.postVisitDirectory(dir, exc);

				if (dir.equals(currentMetadataPath)) {
					currentMetadata = DEFAULT_METADATA;
					currentMetadataPath = null;
				}

				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

				if (attrs.isRegularFile() && isJsonFile(file)) {
					parseReport(file);
				}

				return FileVisitResult.CONTINUE;
			}

		});

	}

	private void parseMetadata(Path path) throws IOException {

		File metadataFile = path.resolve(METADATA_FILENAME).toFile();

		if (metadataFile.exists() && metadataFile.isFile()) {

			try (Reader reader = Files.newBufferedReader(metadataFile.toPath(), StandardCharsets.UTF_8)) {
				Properties props = new Properties();
				props.load(reader);

				currentMetadata = mapper.valueToTree(props);
				currentMetadataPath = path;
			}

		}

	}

	private boolean isJsonFile(Path path) {
		return path.getFileName().toString().endsWith(".json");
	}

	private void parseReport(Path reportFile) throws IOException {

		try (InputStream is = Files.newInputStream(reportFile)) {
			JsonNode node = mapper.readTree(is);

			ArrayNode reportNode = checkReportTree(node);

			if (reportNode.size() == 0) {
				log.warn("Ignoring empty report file {}", reportFile);
				return;
			}

			if (debugMode) {
				checkReportFile(reportNode, reportFile);
			}

			add(reportNode);
		} catch (JsonParseException e) {
			throw e.withRequestPayload(new PathRequestPayload(reportFile));
		}

	}

	private ArrayNode checkReportTree(JsonNode report) {

		if (report.isArray()) {
			return (ArrayNode) report;
		}

		throw new IllegalArgumentException("Report should be an array");

	}

	private void checkReportFile(ArrayNode reportNode, Path reportFile) throws JsonMappingException {
		try {
			mapper.convertValue(reportNode, FEATURES_TYPE_REF);
		} catch (IllegalArgumentException e) {
			throw JsonMappingException.wrapWithPath(e, null, reportFile.toString());
		}
	}

	private void add(ArrayNode reportNode) {

		if (reports == null) {
			reports = reportNode;
			reportsIndex = indexFeatures(reports);
			return;
		}

		Map<String, JsonNode> nodeIndex = indexFeatures(reportNode);

		for (Entry<String, JsonNode> feature : nodeIndex.entrySet()) {

			String key = feature.getKey();
			JsonNode value = feature.getValue();

			if (reportsIndex.containsKey(key)) {
				addScenarios(reportsIndex.get(key), value);
			} else {
				reports.add(value);
				reportsIndex.put(key, value);
			}

		}

	}

	private Map<String, JsonNode> indexFeatures(ArrayNode rootNode) {

		Map<String, JsonNode> features = new HashMap<>();

		for (JsonNode featureNode : rootNode) {
			if (currentMetadata.isObject()) {
				((ObjectNode) featureNode).set(METADATA_FIELD_NAME, currentMetadata);
			}

			features.put(buildFeatureKey(featureNode, currentMetadata), featureNode);
		}

		return features;
	}

	private static String buildFeatureKey(JsonNode featureNode, JsonNode currentMetadata) {

		String key = featureNode.get(FEATURE_URI_FIELD_NAME).textValue();
		return (currentMetadata.isObject()) ? (key + currentMetadata.toString()) : key;

	}

	private void addScenarios(JsonNode accFeatureNode, JsonNode featureNode) {

		ArrayNode scenariosNode = (ArrayNode) accFeatureNode.get(ELEMENTS_FIELD_NAME);
		ArrayNode newScenariosNode = (ArrayNode) featureNode.get(ELEMENTS_FIELD_NAME);

		scenariosNode.addAll(newScenariosNode);

	}

}
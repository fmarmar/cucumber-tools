package org.fmarmar.cucumber.tools.report;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fmarmar.cucumber.tools.report.model.Feature;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * 
 * 
 * 
 * @author fmarmar
 *
 */
public class ReportParser {
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	private final TypeReference<List<Feature>> featuresTypeReference = new TypeReference<List<Feature>>() {};
	
	public ReportParser() {
		configureObjectMapper();
	}

	private void configureObjectMapper() {

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// this prevents printing eg. 2.20 as 2.2
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
		
	}
	
	public List<Feature> parse(Collection<Path> reports) throws IOException {
		return parse(reports.toArray(new Path[reports.size()]));
	}
	
	public List<Feature> parse(Path... reports) throws IOException {
		
		ParserProcess process = new ParserProcess();
		process.parse(reports);
		
		return mapper.convertValue(process.reports, featuresTypeReference);
	}
	
	private class ParserProcess {
		
		private static final String FEATURE_URI_FIELD_NAME = "uri";
		
		private static final String ELEMENTS_FIELD_NAME = "elements";
		
		private ArrayNode reports = null;
		private Map<String, JsonNode> reportsIndex;
		
		private void parse(Path... paths) throws IOException {

			for (Path path : paths) {
				parseOne(path);
			}
		
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
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

					if (attrs.isRegularFile() && isJsonFile(file)) {
						parseReport(file);
					}

					return FileVisitResult.CONTINUE;
				}

			});
			
		}
		
		private void parseReport(Path reportFile) throws IOException {
			
			try (InputStream is = Files.newInputStream(reportFile)) {
				JsonNode reportNode = mapper.readTree(is);
				merge(reportNode);
			}
			
		}

		private void merge(JsonNode reportNode) {
			
			checkReportTree(reportNode);
			
			if (reports == null) {
				reports = (ArrayNode) reportNode;
				reportsIndex = indexFeatures(reports);
				return;
			}
			
			Map<String, JsonNode> nodeIndex = indexFeatures((ArrayNode) reportNode);

			for (Entry<String, JsonNode> feature : nodeIndex.entrySet()) {
				
				String key = feature.getKey();
				JsonNode value = feature.getValue();
				
				if (reportsIndex.containsKey(key)) {
					mergeFeature(reportsIndex.get(key), value);
				} else {
					reports.add(value);
					reportsIndex.put(key, value);			
				}
				
			}
			
		}
		
		private void checkReportTree(JsonNode report) {
			
			if (!report.isArray()) {
				throw new IllegalArgumentException("Report should be an array");
			}
			
		}
		
		private Map<String, JsonNode> indexFeatures(ArrayNode rootNode) {
			
			Map<String, JsonNode> features = new HashMap<>();
			
			for (JsonNode featureNode : rootNode) {
				features.put(buildFeatureKey(featureNode), featureNode);			
			}
			
			return features;
		}

		private String buildFeatureKey(JsonNode featureNode) {
			return featureNode.get(FEATURE_URI_FIELD_NAME).textValue();
		}
		
		private void mergeFeature(JsonNode accFeatureNode, JsonNode featureNode) {

			ArrayNode scenariosNode = (ArrayNode) accFeatureNode.get(ELEMENTS_FIELD_NAME);
			ArrayNode newScenariosNode = (ArrayNode) featureNode.get(ELEMENTS_FIELD_NAME);
			
			scenariosNode.addAll(newScenariosNode);
			
		}
		
	}
	
	private static boolean isJsonFile(Path path) {
		return path.getFileName().toString().endsWith(".json");
	}
	
}
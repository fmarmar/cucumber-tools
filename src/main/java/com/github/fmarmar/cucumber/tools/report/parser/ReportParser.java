package com.github.fmarmar.cucumber.tools.report.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fmarmar.cucumber.tools.report.model.Feature;

/**
 *
 *
 *
 * @author fmarmar
 *
 */
public class ReportParser {

	public static final String CONFIG_ID = "configuration";

	private final ObjectMapper mapper = new ObjectMapper();

	private final ParserConfiguration config;

	private final boolean debugMode;

	public ReportParser() throws IOException {
		this(false);
	}

	public ReportParser(boolean debugMode) throws IOException {
		this.debugMode = debugMode;
		config = new ParserConfiguration();
		configureObjectMapper();
	}

	private void configureObjectMapper() {

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// this prevents printing eg. 2.20 as 2.2
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

        // pass configuration to deserializers
        InjectableValues values = new InjectableValues.Std().addValue(CONFIG_ID, config);
        mapper.setInjectableValues(values);
	}

	public ParsedReports parse(Collection<Path> reports) throws IOException {
		return parse(reports.toArray(new Path[reports.size()]));
	}

	public ParsedReports parse(Path... reports) throws IOException {

		Path embeddingsDirectory = config.newEmbeddingsDirectory();

		ParserTask task = new ParserTask(mapper, debugMode);
		List<Feature> features = task.run(reports);

		return new ParsedReports(features, embeddingsDirectory);
	}

	public static class ParserConfiguration {

		private final Path defaultEmbeddingsDirectory;

		public Map<Long, Path> directoriesIndex = new HashMap<>();

		private ParserConfiguration() throws IOException {
			defaultEmbeddingsDirectory = Files.createTempDirectory("default-embeddings-");
		}

		private Path newEmbeddingsDirectory() throws IOException {
			Path embeddingsDirectory = Files.createTempDirectory("embeddings-");
			directoriesIndex.put(Thread.currentThread().getId(), embeddingsDirectory);
			return embeddingsDirectory;
		}

		public Path getEmbeddingDirectory() {

			Path dir = directoriesIndex.get(Thread.currentThread().getId());
			if (dir == null) {
				return defaultEmbeddingsDirectory;
			}

			return dir;

		}
	}



}
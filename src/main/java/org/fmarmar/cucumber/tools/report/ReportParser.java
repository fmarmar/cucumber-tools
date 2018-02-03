package org.fmarmar.cucumber.tools.report;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.fmarmar.cucumber.tools.report.model.Feature;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		
		return process.features;
	}
	
	
	
	private class ParserProcess {
		
		private List<Feature> features = new ArrayList<>();
		
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
					features.addAll(parseReport(path));
				}
				
			}
			
		}

		private void scanDirectory(Path dir) throws IOException {

			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

					if (attrs.isRegularFile() && isJsonFile(file)) {
						features.addAll(parseReport(file));
					}

					return FileVisitResult.CONTINUE;
				}

			});
			
		}
		
		private Collection<Feature> parseReport(Path report) throws IOException {
			
			try (InputStream is = Files.newInputStream(report)) {
				return mapper.readValue(is, featuresTypeReference);
			}
			
		}
		
	}
	
	private static boolean isJsonFile(Path path) {
		return path.getFileName().toString().endsWith(".json");
	}
	
}
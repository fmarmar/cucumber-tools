package org.fmarmar.cucumber.tools.report.html;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fmarmar.cucumber.tools.Command;
import org.fmarmar.cucumber.tools.common.PathConverter;
import org.fmarmar.cucumber.tools.exception.CommandException;
import org.fmarmar.cucumber.tools.report.ReportParser;
import org.fmarmar.cucumber.tools.report.model.Feature;
import org.fmarmar.cucumber.tools.report.model.support.ReportSummary;
import org.fmarmar.cucumber.tools.rerun.RerunFileConverter;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.io.ByteStreams;

@Parameters(commandNames = "html-report", commandDescription = "")
public class HtmlReport implements Command {

	private static final Path DEFAULT_OUTPUT = Paths.get("reports", "html");
	
	private static final String BASE_RESOURCES = "report/html/DEFAULT/";
	
	private static final String BASE_TEMPLATES = BASE_RESOURCES + "templates/";
	
	private static final String BASE_STATIC = BASE_RESOURCES + "static/";
	
	@Parameter(names = { "--reports", "-r" }, variableArity = true, description = "", converter = PathConverter.class)
	private List<Path> reports;
	
	@Parameter(names = { "--output", "-o" }, description = "", converter = RerunFileConverter.class)
	private Path output = DEFAULT_OUTPUT;
	
	private ReportParser parser;
	
	private VelocityPageGenerator generator;
	
	@Override
	public void initialize() {
		checkOutput();
		init();
	}
	
	private void checkOutput() {

		File outputFile = output.toFile();

		if (outputFile.exists() && !outputFile.isDirectory()) {
			throw new IllegalArgumentException(output + " is not a directory");
		}

		try {
			Files.createDirectories(output);
		} catch (IOException e) {
			throw new CommandException(e.getMessage(), e);
		}

	}
	
	private void init() {
		parser = new ReportParser();
		generator = new VelocityPageGenerator(BASE_TEMPLATES, output);
	}

	@Override
	public void run() {
		
		try {
			List<Feature> features = parser.parse(reports);
			
			Map<String, Object> model = buildModel(features);
			
			copyStaticResources();
			
			generateReport(model);
			
			
		} catch (IOException e) {
			throw new CommandException(e.getMessage(), e);
		}		
	}
	
	private Map<String, Object> buildModel(List<Feature> features) {
		
		Map<String, Object> model = new HashMap<>();
		model.put("features", features);
		
		ReportSummary summary = new ReportSummary();
		model.put("summary", summary);
		
		for (Feature feature : features) {
			summary.add(feature);
			summary.add(feature.getScenariosSummary());
			summary.add(feature.getStepsSummary());
		}
		
		return model;
		
	}
	
	private void copyStaticResources() throws IOException {
		
		// FIXME make generic
		
		String[] staticResources = {
				"css/bootstrap.min.css",
				"css/cucumber.css",
				"css/font-awesome.min.css",
				"fonts/fontawesome-webfont.eot",
				"fonts/fontawesome-webfont.svg",
				"fonts/fontawesome-webfont.ttf",
				"fonts/fontawesome-webfont.woff",
				"fonts/fontawesome-webfont.woff2",
				"fonts/FontAwesome.otf",
				"fonts/glyphicons-halflings-regular.eot",
				"fonts/glyphicons-halflings-regular.svg",
				"fonts/glyphicons-halflings-regular.ttf",
				"fonts/glyphicons-halflings-regular.woff",
				"fonts/glyphicons-halflings-regular.woff2",
				"images/favicon.png",
				"js/bootstrap.min.js",
				"js/Chart.min.js",
				"js/jquery.min.js",
				"js/jquery.tablesorter.min.js",
				"js/moment.min.js"
		};
		
		for (String resource : staticResources) {
			copyClasspathResource(BASE_STATIC + resource, output.resolve(resource));
		}
		
	}
	
	private void copyClasspathResource(String resourceLocation, Path destinationPath) throws IOException {
		
		Files.createDirectories(destinationPath.getParent());
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		
		try (InputStream is = cl.getResourceAsStream(resourceLocation); OutputStream os = Files.newOutputStream(destinationPath)) {
			if (is == null) {
				throw new IllegalArgumentException("Resource " + resourceLocation + " not found in classpath");
			}
			ByteStreams.copy(is, os);
		}
		
	}

	private void generateReport(Map<String, Object> model) throws IOException {
		
		generator.generatePage("featuresOverview.vm", output.resolve("features-overview.html"), model);
		
	}
	
	
	
}
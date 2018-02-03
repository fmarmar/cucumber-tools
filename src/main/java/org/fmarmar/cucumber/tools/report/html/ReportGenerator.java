package org.fmarmar.cucumber.tools.report.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fmarmar.cucumber.tools.report.model.Feature;
import org.fmarmar.cucumber.tools.report.model.support.ReportSummary;

import com.google.common.io.ByteStreams;

public class ReportGenerator {
	
	private static final String BASE_RESOURCES = "report/html/DEFAULT/";
	
	private static final String BASE_TEMPLATES = BASE_RESOURCES + "templates/";
	
	private static final String BASE_STATIC = BASE_RESOURCES + "static/";
	
	private final Path output;
	
	private final VelocityPageGenerator pageGenerator;
	
	public ReportGenerator(Path output) {
		this.output = output;
		pageGenerator = new VelocityPageGenerator(BASE_TEMPLATES, output);		
	}
	
	public void prepareReport() throws IOException {
		
		Files.createDirectories(output);
		Files.createDirectories(output.resolve("features"));
		
		copyStaticResources();
		
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
	
	public void generateReport(List<Feature> features) throws IOException {
		
		Map<String, Object> featureOvervieModel = buildBaseModel(".");
		featureOvervieModel.put("features", features);
		
		ReportSummary summary = new ReportSummary();
		featureOvervieModel.put("summary", summary);
		
		for (Feature feature : features) {
			
			// featureOvervie model
			summary.add(feature);
			summary.add(feature.getScenariosSummary());
			summary.add(feature.getStepsSummary());
			
			generateFeaturePage(feature);
			
		}
		
		pageGenerator.generatePage("featuresOverview.vm", output.resolve("features-overview.html"), featureOvervieModel);
		
	}

	private void generateFeaturePage(Feature feature) throws IOException {
		
		Path featurePath = output.resolve("features").resolve(feature.getUuid() + ".html");
		
		Map<String, Object> featureModel = buildBaseModel("..");
		featureModel.put("feature", feature);
			
		pageGenerator.generatePage("feature.vm", featurePath, featureModel);
		
	}
	
	private Map<String, Object> buildBaseModel(String basePath) {
		
		Map<String, Object> model = new HashMap<>();
		model.put("basePath", basePath);
		
		return model;
	}
	
}
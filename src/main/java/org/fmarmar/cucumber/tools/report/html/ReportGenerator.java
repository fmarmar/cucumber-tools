package org.fmarmar.cucumber.tools.report.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.fmarmar.cucumber.tools.report.html.report.FailuresReport;
import org.fmarmar.cucumber.tools.report.html.report.StepsReport;
import org.fmarmar.cucumber.tools.report.html.report.TagsReport;
import org.fmarmar.cucumber.tools.report.model.Feature;
import org.fmarmar.cucumber.tools.report.model.Scenario;
import org.fmarmar.cucumber.tools.report.model.support.ReportSummary;

import com.google.common.io.ByteStreams;

import lombok.AllArgsConstructor;

public class ReportGenerator {

	private static final String BASE_RESOURCES = "report/html/DEFAULT/";

	private static final String BASE_TEMPLATES = BASE_RESOURCES + "templates/";

	private static final String BASE_STATIC = BASE_RESOURCES + "static/";
	
	private static final int DEFAULT_THREADS_SIZE = 100;

	private final Path output;

	private final VelocityPageGenerator pageGenerator;

	private final ExecutorService executor;
	
	private final Collection<Future<Void>> tasks = new ArrayList<>();

	public ReportGenerator(Path output) {
		this.output = output;
		pageGenerator = new VelocityPageGenerator(BASE_TEMPLATES, DEFAULT_THREADS_SIZE);
		executor = Executors.newFixedThreadPool(DEFAULT_THREADS_SIZE);
	}

	public void prepareReport() throws IOException {

		FileUtils.deleteQuietly(output.toFile());
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
			executeTask(new CopyClasspathResourceTask(BASE_STATIC + resource, output.resolve(resource)));
		}

	}

	public void generateReport(List<Feature> features) throws IOException {

		Map<String, Object> featuresOvervieModel = buildFeatureOverviewModel(features);
		ReportSummary summary = (ReportSummary) featuresOvervieModel.get("summary");
		
		TagsReport tagsReport = new TagsReport();
		StepsReport stepsReport = new StepsReport();
		FailuresReport failuresReport = new FailuresReport();
		
		for (Feature feature : features) {

			// featuresOverview model
			summary.add(feature);
			summary.add(feature.getScenariosSummary());
			summary.add(feature.getStepsSummary());

			executeTask(generateFeaturePage(feature));

			collectFeatureInfo(feature, summary, tagsReport, stepsReport, failuresReport);

		}

		executeTask(generateFeaturesOverviewPage(featuresOvervieModel));
		
		//executeTask(generateTagsOverviewPage(tagsReport));

	}

	private Map<String, Object> buildFeatureOverviewModel(List<Feature> features) {
		
		Map<String, Object> featuresOvervieModel = buildBaseModel(".");
		featuresOvervieModel.put("features", features);

		ReportSummary summary = new ReportSummary();
		featuresOvervieModel.put("summary", summary);
		
		return featuresOvervieModel;
	}

	private GeneratePageTask generateFeaturePage(Feature feature) throws IOException {

		Path path = output.resolve("features").resolve(feature.getUuid() + ".html");

		Map<String, Object> featureModel = buildBaseModel("..");
		featureModel.put("feature", feature);

		return new GeneratePageTask("feature.vm", path, featureModel);

	}
	
	private void collectFeatureInfo(Feature feature, ReportSummary summary, TagsReport tagsReport, StepsReport stepsReport, FailuresReport failuresReport) {
		
		// ReportSummary
		summary.add(feature);
		summary.add(feature.getScenariosSummary());
		summary.add(feature.getStepsSummary());
		
		for (Scenario scenario : feature.getScenarios()) {
			
			tagsReport.collectTagsInfo(scenario.getTags(), scenario);
			
		}
		
	}
	
	private GeneratePageTask generateFeaturesOverviewPage(Map<String, Object> featuresOvervieModel) throws IOException {

		Path path = output.resolve("features-overview.html");
		return new GeneratePageTask("featuresOverview.vm", path, featuresOvervieModel);

	}
	
	private GeneratePageTask generateTagsOverviewPage(TagsReport report) throws IOException {

		Path path = output.resolve("tags-overview.html");
		
		Map<String, Object> tagsOvervieModel = buildBaseModel(".");
		tagsOvervieModel.put("tags", report.getTags());
		tagsOvervieModel.put("tagNames", report.getTagNames());
		
		return new GeneratePageTask("tagsOverview.vm", path, tagsOvervieModel);

	}

	private Map<String, Object> buildBaseModel(String basePath) {

		Map<String, Object> model = new HashMap<>();
		model.put("basePath", basePath);

		return model;
	}
	
	private void executeTask(Callable<Void> task) {
		tasks.add(executor.submit(task));
	}
	
	public void finishReport() throws IOException {
		
		executor.shutdown();
		
		Collection<Exception> errors = new ArrayList<>();
		
		for (Future<Void> task : tasks) {
			try {
				task.get();
			} catch (InterruptedException | ExecutionException e) {
				errors.add(e);
			}
		}
		
	}
	
	@AllArgsConstructor
	private static class CopyClasspathResourceTask implements Callable<Void> {

		private final String resourceLocation;

		private final Path outputPath;

		@Override
		public Void call() throws Exception {
			
			Files.createDirectories(outputPath.getParent());
			ClassLoader cl = Thread.currentThread().getContextClassLoader();

			try (InputStream is = cl.getResourceAsStream(resourceLocation); OutputStream os = Files.newOutputStream(outputPath)) {
				if (is == null) {
					throw new IllegalArgumentException("Resource " + resourceLocation + " not found in classpath");
				}
				ByteStreams.copy(is, os);
			}
			
			return null;
		}

	}

	@AllArgsConstructor
	private class GeneratePageTask implements Callable<Void> {

		private final String templateName;

		private final Path outputPath;

		private final Map<String, Object> model;

		@Override
		public Void call() throws Exception {
			pageGenerator.generatePage(templateName, outputPath, model);
			return null;
		}

	}

}
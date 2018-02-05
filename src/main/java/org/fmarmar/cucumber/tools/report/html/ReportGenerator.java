package org.fmarmar.cucumber.tools.report.html;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.fmarmar.cucumber.tools.report.html.page.PageGenerator;
import org.fmarmar.cucumber.tools.report.html.page.PageGenerator.PageId;
import org.fmarmar.cucumber.tools.report.html.report.FailuresReport;
import org.fmarmar.cucumber.tools.report.html.support.AlphabeticalComparator;
import org.fmarmar.cucumber.tools.report.model.Feature;
import org.fmarmar.cucumber.tools.report.model.Scenario;
import org.fmarmar.cucumber.tools.report.model.support.ReportSummary;

import lombok.AllArgsConstructor;

public class ReportGenerator {
	
	private final Path output;

	private final PageGenerator pageGenerator;

	private final ExecutorService executor;
	
	private final Collection<Future<Void>> tasks = new ArrayList<>();

	public ReportGenerator(PageGenerator pageGenerator, Path output, int poolSize) {
		this.output = output;
		this.pageGenerator = pageGenerator;
		executor = Executors.newFixedThreadPool(poolSize);
	}

	public void prepareReport() throws IOException {

		FileUtils.deleteQuietly(output.toFile());
		Files.createDirectories(output);

		pageGenerator.initialize(output);
		executeTask(new CopyStaticResourcesTask());

	}

	public void generateReport(List<Feature> features) throws IOException {
		
		Collections.sort(features, AlphabeticalComparator.INSTANCE);

		ReportSummary summary = new ReportSummary();
		FailuresReport failuresReport = new FailuresReport();
		
		for (Feature feature : features) {

			Collections.sort(feature.getScenarios(), AlphabeticalComparator.INSTANCE);
			
			executeTask(generateFeaturePage(feature));

			collectFeatureInfo(feature, summary, failuresReport);

		}

		executeTask(generateFeaturesOverviewPage(features, summary));

	}

	private void collectFeatureInfo(Feature feature, ReportSummary summary, FailuresReport failuresReport) {
		
		// ReportSummary
		summary.add(feature);
		summary.add(feature.getScenariosSummary());
		summary.add(feature.getStepsSummary());
		
		for (Scenario scenario : feature.getScenarios()) {
			
			
		}
		
	}

	private GeneratePageTask generateFeaturePage(Feature feature) throws IOException {

		Path path = output.resolve(pageGenerator.resolvePagePath(PageId.FEATURE, feature.getUuid()));

		Map<String, Object> featureModel = new HashMap<>();
		featureModel.put("feature", feature);

		return new GeneratePageTask(PageId.FEATURE, path, featureModel);

	}
	
	private GeneratePageTask generateFeaturesOverviewPage(List<Feature> features, ReportSummary summary) throws IOException {

		Path path = output.resolve(pageGenerator.resolvePagePath(PageId.FEATURES_OVERVIEW));
		
		Map<String, Object> featuresOvervieModel = new HashMap<>();
		featuresOvervieModel.put("features", features);
		featuresOvervieModel.put("summary", summary);
		
		return new GeneratePageTask(PageId.FEATURES_OVERVIEW, path, featuresOvervieModel);

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
				// FIXME we should throw the errors
				errors.add(e);
			}
		}
		
	}
	
	@AllArgsConstructor
	private class CopyStaticResourcesTask implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			pageGenerator.copyStaticResources(output);
			return null;
		}

	}

	@AllArgsConstructor
	private class GeneratePageTask implements Callable<Void> {

		private final PageId pageId;

		private final Path outputPath;

		private final Map<String, Object> model;

		@Override
		public Void call() throws Exception {
			pageGenerator.generatePage(pageId, outputPath, model);
			return null;
		}

	}

}
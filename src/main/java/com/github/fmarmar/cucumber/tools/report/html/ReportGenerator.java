package com.github.fmarmar.cucumber.tools.report.html;

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

import com.github.fmarmar.cucumber.tools.exception.MultiException;
import com.github.fmarmar.cucumber.tools.report.html.page.PageGenerator;
import com.github.fmarmar.cucumber.tools.report.html.page.PageGenerator.PageId;
import com.github.fmarmar.cucumber.tools.report.html.report.FailuresReport;
import com.github.fmarmar.cucumber.tools.report.html.report.FeaturesReport;
import com.github.fmarmar.cucumber.tools.report.html.report.TagsReport;
import com.github.fmarmar.cucumber.tools.report.html.support.AlphabeticalComparator;
import com.github.fmarmar.cucumber.tools.report.model.Feature;
import com.github.fmarmar.cucumber.tools.report.model.Scenario;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericStatus;
import com.google.common.collect.Iterables;

import lombok.AllArgsConstructor;

public class ReportGenerator {
	
	public static final String FEATURES_REPORT_KEY = "featuresReport";
	
	public static final String FAILURES_KEY = "failures";
	
	public static final String TAGS_REPORT_KEY = "tagsReport";
	
	private final Path output;

	private final PageGenerator pageGenerator;

	private final ExecutorService executor;
	
	private final Collection<Future<Void>> tasks = new ArrayList<>();

	public ReportGenerator(PageGenerator pageGenerator, Path output, int poolSize) {
		this.output = output;
		this.pageGenerator = pageGenerator;
		executor = Executors.newFixedThreadPool(poolSize);
	}

	public void prepareReport(Path embeddingsDirectory) throws IOException {

		FileUtils.deleteQuietly(output.toFile());
		Files.createDirectories(output);

		pageGenerator.initialize(output);
		executeTask(new CopyStaticResourcesTask());
		executeTask(new CopyEmbeddingsTask(embeddingsDirectory));

	}

	public void generateReport(List<Feature> features) throws IOException {
		
		Collections.sort(features, AlphabeticalComparator.INSTANCE);

		FeaturesReport featuresReport = new FeaturesReport();
		TagsReport tagsReport = new TagsReport();
		FailuresReport failuresReport = new FailuresReport();
		
		for (Feature feature : features) {

			Collections.sort(feature.getScenarios(), AlphabeticalComparator.INSTANCE);
			
			executeTask(generateFeaturePage(feature));

			collectFeatureInfo(feature, featuresReport, tagsReport, failuresReport);

		}

		executeTask(generateFeaturesOverviewPage(features, featuresReport));
		executeTask(generateTagsPage(tagsReport));
		executeTask(generateFailuresPage(failuresReport));

	}

	private void collectFeatureInfo(Feature feature, FeaturesReport featuresReport, TagsReport tagsReport, FailuresReport failuresReport) {
		
		// Features report
		featuresReport.add(feature);
		
		for (Scenario scenario : feature.getScenarios()) {
			
			tagsReport.collectTagsInfo(Iterables.concat(feature.getTags(), scenario.getTags()), scenario);
			
			if (scenario.getStatus() == GenericStatus.FAILED) {
				failuresReport.addFailure(feature, scenario);
			}
			
		}
		
	}

	private GeneratePageTask generateFeaturePage(Feature feature) throws IOException {

		Path path = output.resolve(pageGenerator.resolvePagePath(PageId.FEATURE, feature.getUuid()));

		Map<String, Object> model = new HashMap<>();
		model.put("feature", feature);

		return new GeneratePageTask(PageId.FEATURE, path, model);

	}
	
	private Callable<Void> generateFeaturesOverviewPage(List<Feature> features, FeaturesReport featuresReport) throws IOException {

		Path path = output.resolve(pageGenerator.resolvePagePath(PageId.FEATURES_OVERVIEW));
		
		Map<String, Object> model = new HashMap<>();
		model.put("features", features);
		model.put(FEATURES_REPORT_KEY, featuresReport);
		
		return new GeneratePageTask(PageId.FEATURES_OVERVIEW, path, model);

	}
	
	private Callable<Void> generateTagsPage(TagsReport tagsReport) {
		
		Path path = output.resolve(pageGenerator.resolvePagePath(PageId.TAGS_OVERVIEW));
		
		Map<String, Object> model = new HashMap<>();
		model.put(TAGS_REPORT_KEY, tagsReport);
		
		return new GeneratePageTask(PageId.TAGS_OVERVIEW, path, model);
		
	}
	
	private Callable<Void> generateFailuresPage(FailuresReport failuresReport) throws IOException {

		Path path = output.resolve(pageGenerator.resolvePagePath(PageId.FAILURES_OVERVIEW));
		
		Map<String, Object> model = new HashMap<>();
		model.put(FAILURES_KEY, failuresReport.getFailures());
		
		return new GeneratePageTask(PageId.FAILURES_OVERVIEW, path, model);

	}
	
	private void executeTask(Callable<Void> task) {
		tasks.add(executor.submit(task));
	}
	
	public void finishReport() throws IOException, InterruptedException {
		
		executor.shutdown();
		
		Collection<Throwable> errors = new ArrayList<>();
		
		for (Future<Void> task : tasks) {
			try {
				task.get();
			} catch (ExecutionException e) {
				errors.add(e.getCause());
			}
		}
		
		if (!errors.isEmpty()) {
			throw MultiException.newInstance(errors);
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
	private class CopyEmbeddingsTask implements Callable<Void> {

		private final Path embeddingsDirectory;
		
		@Override
		public Void call() throws Exception {
			pageGenerator.copyEmbeddings(embeddingsDirectory, output);
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
package com.github.fmarmar.cucumber.tools.report.csv;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import com.github.fmarmar.cucumber.tools.report.csv.CsvReport.ReportType;
import com.github.fmarmar.cucumber.tools.report.model.Feature;
import com.github.fmarmar.cucumber.tools.report.support.AlphabeticalComparator;
import com.github.fmarmar.cucumber.tools.report.support.model.FeaturesSummary;
import com.google.common.base.Joiner;

class Reporter {

	private final Path csvFile;

	private final ReportType type;

	private final Joiner joiner = Joiner.on(';');

	public Reporter(Path csvFile, ReportType type) {
		this.csvFile = csvFile;
		this.type = type;
	}

	public void generateReport(List<Feature> features) throws IOException {

		switch (type) {
			case FEATURES:
				generateFeaturesReport(features);
				break;
			default:
				throw new IllegalStateException("Unknown csv report type " + type);
		}

	}

	private void generateFeaturesReport(List<Feature> features) throws IOException {

		Collections.sort(features, AlphabeticalComparator.INSTANCE);

		try (Writer writer = Files.newBufferedWriter(csvFile, StandardCharsets.UTF_8)) {

			StringBuilder line = new StringBuilder(200);
			FeaturesSummary featuresReport = new FeaturesSummary();

			for (Feature feature : features) {

				featuresReport.add(feature);

				joiner.appendTo(line,
						feature.getName(),
						feature.getUri(),
						feature.getScenariosSummary().getPassed(),
						feature.getScenariosSummary().getSkipped(),
						feature.getScenariosSummary().getFailed(),
						feature.getScenariosSummary().getTotal(),
						feature.getStatus().getLabel());

				writer.append(line).append('\n');
				line.setLength(0);

			}

			writer.append("----------------------------------------\n");
			writer.append(";;");

			joiner.appendTo(line,
					featuresReport.getScenarios().getPassed(),
					featuresReport.getScenarios().getSkipped(),
					featuresReport.getScenarios().getFailed(),
					featuresReport.getScenarios().getTotal());

			writer.append(line).append(';').append('\n');
		}

	}





}
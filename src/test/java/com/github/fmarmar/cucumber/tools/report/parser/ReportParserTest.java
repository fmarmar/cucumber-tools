package com.github.fmarmar.cucumber.tools.report.parser;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.fmarmar.cucumber.tools.report.model.ExecutionElement;
import com.github.fmarmar.cucumber.tools.report.model.Feature;
import com.github.fmarmar.cucumber.tools.report.model.Metadata;
import com.github.fmarmar.cucumber.tools.report.model.ModelTestUtils;
import com.github.fmarmar.cucumber.tools.report.model.Scenario;
import com.github.fmarmar.cucumber.tools.report.model.Step;
import com.github.fmarmar.cucumber.tools.report.model.support.ScenarioType;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ReportParserTest {
	
	private static final String BASE_PATH = "src/test/resources/com/github/fmarmar/cucumber/tools/report/examples/";
	
	private ReportParser parser;
	
	@Before
	public void configureTest() throws IOException {
		parser = new ReportParser();
	}
	
	@Test
	public void testParseRubyFeatureBackground() throws IOException {
		
		Path report = Paths.get(BASE_PATH + "ruby-feature-background.json");
		
		ParsedReports parsedReports = parser.parse(report);
		List<Feature> features = parsedReports.getFeatures();
		
		assertThat(features, hasSize(greaterThan(0)));
		
		Feature feature = features.get(0);
		Collection<String> featureTags =  feature.getTags();
		assertThat(featureTags, hasSize(greaterThan(0)));
		
		Collection<Scenario> scenarios = feature.getScenarios();
		
		assertThat(scenarios, contains(hasProperty("type", not(equalTo(ScenarioType.BACKGROUND)))));
		assertThat(scenarios, contains(hasProperty("backgroundName", not(nullValue()))));
		assertThat(scenarios, contains(hasProperty("backgroundSteps", not(empty()))));
		assertThat(scenarios, contains(hasProperty("steps", not(empty()))));
		assertThat(scenarios, contains(hasProperty("tags", hasItems(featureTags.toArray()))));
		assertThat(scenarios, contains(hasProperty("result", not(nullValue()))));

	}
	
	@Test
	public void testParseJsReport() throws IOException {
		
		Path report = Paths.get(BASE_PATH + "js-example.json");
		
		ParsedReports parsedReports = parser.parse(report);
		List<Feature> features = parsedReports.getFeatures();
		
		assertThat(features, hasSize(greaterThan(0)));
		
		Feature feature = features.get(0);
		Collection<String> featureTags =  feature.getTags();
		assertThat(featureTags, hasSize(greaterThan(0)));
		
		Collection<Scenario> scenarios = feature.getScenarios();
		
		assertThat(scenarios, hasSize(greaterThan(0)));
		assertThat(scenarios, contains(hasProperty("type", equalTo(ScenarioType.SCENARIO))));
		assertThat(scenarios, contains(not(hasProperty("backgroundName"))));
		assertThat(scenarios, contains(not(hasProperty("backgroundSteps"))));
		assertThat(scenarios, contains(hasProperty("before", not(empty()))));
		assertThat(scenarios, contains(hasProperty("after", not(empty()))));
		assertThat(scenarios, contains(hasProperty("tags", hasItems(featureTags.toArray()))));
		assertThat(scenarios, contains(hasProperty("result", not(nullValue()))));
		
		Scenario scenario = Iterables.getLast(scenarios);

		for (Step step : scenario.getSteps()) {
			assertThat(step.getDuration(), anyOf(greaterThanOrEqualTo(1000000L), equalTo(0L)));
		}
		
	}
	
	@Test
	public void testMixedReportsDurationNanoseconds() throws IOException {
	
		Path jsReport = Paths.get(BASE_PATH + "js-fixed-duration.json");
		Path rubyReport = Paths.get(BASE_PATH + "ruby-fixed-duration.json");
		
		ParsedReports parsedReports = parser.parse(jsReport, rubyReport);
		List<Feature> features = parsedReports.getFeatures();
		
		for (Feature feature: features) {
			for (Scenario scenario : feature.getScenarios()) {
				for (ExecutionElement step : ModelTestUtils.steps(scenario)) {
					assertThat(step.getDuration(), equalTo(1000000L));
				}
			}
		}
	}
	
	@Test
	public void testNoMetadata() throws IOException {
	
		Path reportDir = Paths.get(BASE_PATH + "nometadata");
				
		ParsedReports parsedReports = parser.parse(reportDir);
		List<Feature> features = parsedReports.getFeatures();
		
		assertThat(features, contains(hasProperty("metadata", equalTo(Metadata.NO_METADATA_INSTANCE))));
		
	}
	
	@Test
	public void testMetadataIsRead() throws IOException {
	
		Path reportDir = Paths.get(BASE_PATH + "metadata");
				
		ParsedReports parsedReports = parser.parse(reportDir);
		List<Feature> features = parsedReports.getFeatures();
		
		assertThat(features, contains(hasProperty("metadata", not(equalTo(Metadata.NO_METADATA_INSTANCE)))));
		
	}
	
	@Test
	public void testMixedMetadata() throws IOException {
	
		Path reportDir = Paths.get(BASE_PATH + "mixedmetadata");
				
		ParsedReports parsedReports = parser.parse(reportDir);
		List<Feature> features = parsedReports.getFeatures();
		
		assertThat(features, hasSize(greaterThan(1)));
		
		boolean noMetadataFound = false;
		boolean metadataFound = false;
		
		for (Feature feature : features) {
			if (feature.getMetadata().empty()) {
				noMetadataFound = true;
			} else {
				metadataFound = true;
			}
		}
		
		assertThat("Features with metadata and without metadata should be found", (metadataFound && noMetadataFound), equalTo(true));
	}
	
	@Test
	public void testSameFeatureMultipleMetadata() throws IOException {
	
		Path reportDir = Paths.get(BASE_PATH + "multiplemetadata");
				
		ParsedReports parsedReports = parser.parse(reportDir);
		List<Feature> features = parsedReports.getFeatures();
		
		assertThat(features, hasSize(greaterThan(1)));
		
		Collection<String> metadatas = Lists.newArrayList("windows", "mac", "linux");
		
		for (Feature feature : features) {
			assertThat(feature.getMetadata(), not(equalTo(Metadata.NO_METADATA_INSTANCE)));
			metadatas.remove(feature.getMetadata().getOs());
		}
		
		assertThat("No feature found with os declared in metadata: " + metadatas.toString(), metadatas, hasSize(equalTo(0)));
		
	}
	
}
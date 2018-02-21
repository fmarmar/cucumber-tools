package com.github.fmarmar.cucumber.tools.report.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.filter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.assertj.core.condition.AnyOf;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.fmarmar.assertj.Conditions;
import com.github.fmarmar.cucumber.tools.TestUtils;
import com.github.fmarmar.cucumber.tools.report.model.Feature;
import com.github.fmarmar.cucumber.tools.report.model.Metadata;
import com.github.fmarmar.cucumber.tools.report.model.ModelTestUtils;
import com.github.fmarmar.cucumber.tools.report.model.Scenario;
import com.github.fmarmar.cucumber.tools.report.model.ScenarioWithBackground;
import com.github.fmarmar.cucumber.tools.report.model.support.ScenarioType;
import com.google.common.collect.Iterables;

public class ReportParserTest {

	private ReportParser parser;

	@Before
	public void configureTest() throws IOException {
		parser = new ReportParser();
	}

	@Test
	public void testParseRubyFeatureBackground() throws IOException {

		Path report = TestUtils.REPORTS_BASE_PATH.resolve("ruby-feature-background.json");

		ParsedReports parsedReports = parser.parse(report);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {

			List<Feature> features = parsedReports.getFeatures();
			softly.assertThat(features).size().isGreaterThan(0);

			Feature feature = features.get(0);
			Collection<String> featureTags =  feature.getTags();
			softly.assertThat(featureTags).size().isGreaterThan(0);

			Collection<Scenario> scenarios = feature.getScenarios();
			softly.assertThat(scenarios).extracting("type").doesNotContain(ScenarioType.BACKGROUND);
			softly.assertThat(scenarios)
				.filteredOn(Conditions.instanceOf(ScenarioWithBackground.class))
				.extracting("backgroundName")
				.doesNotContainNull();

			softly.assertThat(scenarios)
				.filteredOn(Conditions.instanceOf(ScenarioWithBackground.class))
				.extracting("backgroundSteps", List.class)
				.areNot(Conditions.empty());

			softly.assertThat(scenarios).extracting("steps", List.class).areNot(Conditions.empty());
			softly.assertThat(scenarios).extracting("tags", Set.class).are(Conditions.contains(featureTags));
			softly.assertThat(scenarios).extracting("result").doesNotContainNull();

		}

	}

	@Test
	public void testParseJsReport() throws IOException {

		Path report = TestUtils.REPORTS_BASE_PATH.resolve("js-example.json");

		ParsedReports parsedReports = parser.parse(report);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {

			List<Feature> features = parsedReports.getFeatures();
			softly.assertThat(features).size().isGreaterThan(0);

			Feature feature = features.get(0);
			Collection<String> featureTags =  feature.getTags();
			softly.assertThat(featureTags).size().isGreaterThan(0);

			Collection<Scenario> scenarios = feature.getScenarios();
			softly.assertThat(scenarios).size().isGreaterThan(0);
			softly.assertThat(scenarios).areNot(Conditions.instanceOf(ScenarioWithBackground.class));
			softly.assertThat(scenarios).extracting("type", ScenarioType.class).are(Conditions.equalTo(ScenarioType.SCENARIO));
			softly.assertThat(scenarios).extracting("before", List.class).areNot(Conditions.empty());
			softly.assertThat(scenarios).extracting("after", List.class).areNot(Conditions.empty());
			softly.assertThat(scenarios).extracting("tags", Set.class).are(Conditions.contains(featureTags));
			softly.assertThat(scenarios).extracting("result").doesNotContainNull();

			Scenario anScenario = Iterables.getLast(scenarios);
			softly.assertThat(anScenario.getSteps())
				.extracting("duration", Long.class)
				.are(AnyOf.anyOf(Conditions.greaterThanOrEqualTo(1000000L), Conditions.equalTo(0L)));

		}

	}

	@Test
	public void testMixedReportsDurationNanoseconds() throws IOException {

		Path jsReport = TestUtils.REPORTS_BASE_PATH.resolve("js-fixed-duration.json");
		Path rubyReport = TestUtils.REPORTS_BASE_PATH.resolve("ruby-fixed-duration.json");

		ParsedReports parsedReports = parser.parse(jsReport, rubyReport);
		List<Feature> features = parsedReports.getFeatures();

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
		
			for (Feature feature: features) {
				for (Scenario scenario : feature.getScenarios()) {
					softly.assertThat(ModelTestUtils.steps(scenario)).extracting("duration", Long.class).are(Conditions.equalTo(1000000L));
				}
			}
		}
	}

	@Test
	public void testNoMetadata() throws IOException {

		Path reportDir = TestUtils.REPORTS_BASE_PATH.resolve("nometadata");

		ParsedReports parsedReports = parser.parse(reportDir);
		List<Feature> features = parsedReports.getFeatures();

		assertThat(features).extracting("metadata", Metadata.class).are(Conditions.equalTo(Metadata.NO_METADATA_INSTANCE));
	}

	@Test
	public void testMetadataIsRead() throws IOException {

		Path reportDir = TestUtils.REPORTS_BASE_PATH.resolve("metadata");

		ParsedReports parsedReports = parser.parse(reportDir);
		List<Feature> features = parsedReports.getFeatures();
		
		assertThat(features).extracting("metadata", Metadata.class).areNot(Conditions.equalTo(Metadata.NO_METADATA_INSTANCE));

	}

	@Test
	public void testMixedMetadata() throws IOException {

		Path reportDir = TestUtils.REPORTS_BASE_PATH.resolve("mixedmetadata");

		ParsedReports parsedReports = parser.parse(reportDir);
		List<Feature> features = parsedReports.getFeatures();

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			softly.assertThat(features).size().isGreaterThan(1);
			
			softly.assertThat(filter(features).with("metadata").equalsTo(Metadata.NO_METADATA_INSTANCE).get()).size().isGreaterThanOrEqualTo(1);
			softly.assertThat(filter(features).with("metadata").notEqualsTo(Metadata.NO_METADATA_INSTANCE).get()).size().isGreaterThanOrEqualTo(1);
			
		}
		
	}

	@Test
	public void testSameFeatureMultipleMetadata() throws IOException {

		Path reportDir = TestUtils.REPORTS_BASE_PATH.resolve("multiplemetadata");

		ParsedReports parsedReports = parser.parse(reportDir);
		List<Feature> features = parsedReports.getFeatures();

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			softly.assertThat(features).size().isGreaterThan(1);
			
			softly.assertThat(features).extracting("metadata").doesNotContain(Metadata.NO_METADATA_INSTANCE);
			softly.assertThat(features).extracting("metadata.os").containsExactlyInAnyOrder("windows", "mac", "linux");
			
		}
		
	}
	
	@Test
	public void testParseInvalidReportShowFile() throws IOException {
		
		Path invalidReport = TestUtils.REPORTS_BASE_PATH.resolve("invalid.json");
		
		try {
			parser.parse(invalidReport);
			fail("Parser should have failed parsing file: " + invalidReport);
		} catch (JsonParseException e) {
			assertThat(e).hasMessageContaining(invalidReport.toString());
		}
	}

}
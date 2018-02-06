package org.fmarmar.cucumber.tools.report.parser;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
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

import org.fmarmar.cucumber.tools.report.model.Feature;
import org.fmarmar.cucumber.tools.report.model.Scenario;
import org.fmarmar.cucumber.tools.report.model.support.ScenarioType;
import org.fmarmar.cucumber.tools.report.parser.ParsedReports;
import org.fmarmar.cucumber.tools.report.parser.ReportParser;
import org.junit.Before;
import org.junit.Test;

public class ReportParserTest {
	
	private static final String BASE_PATH = "src/test/resources/org/fmarmar/cucumber/tools/report/examples/";
	
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
	
}
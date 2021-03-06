package com.github.fmarmar.cucumber.tools;

import java.nio.file.Paths;
import java.util.Collections;

import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.junit.Test;

import com.beust.jcommander.JCommander;
import com.github.fmarmar.cucumber.tools.jcommander.JcommanderUtils;
import com.github.fmarmar.cucumber.tools.report.html.HtmlReport;
import com.github.fmarmar.cucumber.tools.split.SplitFeatures;
import com.github.fmarmar.cucumber.tools.split.SplitFeatures.Separator;

public class CommandLineTest {

	@Test
	public void testHtmlReportBasic() {

		HtmlReport command = new HtmlReport();
		JCommander jcommander = JcommanderUtils.buildJcommander(null, Collections.singleton((Command) command));

		jcommander.parse("html-report", "--reports", "reports");
		String commandName = jcommander.getParsedCommand();
		
		Command parsedCommand = JcommanderUtils.getCommandObject(jcommander, commandName);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			
			softly.assertThat(commandName).isEqualTo("html-report");
			softly.assertThat(parsedCommand).isEqualTo(command);
			softly.assertThat(command.getReports()).size().isEqualTo(1);
			softly.assertThat(command.getReports()).contains(Paths.get("reports").toAbsolutePath());
			softly.assertThat(command.getOutput()).isEqualTo(HtmlReport.DEFAULT_OUTPUT);
			softly.assertThat(command.getProjectName()).isEqualTo(HtmlReport.DEFAULT_PROJECT_NAME);
			softly.assertThat(command.getBuildId()).isNull();
			
		}
		
	}
	
	@Test
	public void testHtmlReportLongOptions() {

		HtmlReport command = new HtmlReport();
		JCommander jcommander = JcommanderUtils.buildJcommander(null, Collections.singleton((Command) command));

		jcommander.parse("html-report", "--reports", "reports", "--output", "target/", "--project", "test", "--build", "666");
		String commandName = jcommander.getParsedCommand();
		
		Command parsedCommand =  JcommanderUtils.getCommandObject(jcommander, commandName);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			
			softly.assertThat(commandName).isEqualTo("html-report");
			softly.assertThat(parsedCommand).isEqualTo(command);
			softly.assertThat(command.getReports()).size().isEqualTo(1);
			softly.assertThat(command.getReports()).contains(Paths.get("reports").toAbsolutePath());
			softly.assertThat(command.getOutput()).isEqualTo(Paths.get("target").toAbsolutePath());
			softly.assertThat(command.getProjectName()).isEqualTo("test");
			softly.assertThat(command.getBuildId()).isEqualTo("666");
			
		}
		
	}
	
	@Test
	public void testHtmlReportShortOptions() {

		HtmlReport command = new HtmlReport();
		JCommander jcommander = JcommanderUtils.buildJcommander(null, Collections.singleton((Command) command));

		jcommander.parse("html-report", "-r", "reports", "-o", "target", "-p", "test", "-b", "666");
		String commandName = jcommander.getParsedCommand();
		
		Command parsedCommand = JcommanderUtils.getCommandObject(jcommander, commandName);
		
		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			
			softly.assertThat(commandName).isEqualTo("html-report");
			softly.assertThat(parsedCommand).isEqualTo(command);
			softly.assertThat(command.getReports()).size().isEqualTo(1);
			softly.assertThat(command.getReports()).contains(Paths.get("reports").toAbsolutePath());
			softly.assertThat(command.getOutput()).isEqualTo(Paths.get("target").toAbsolutePath());
			softly.assertThat(command.getProjectName()).isEqualTo("test");
			softly.assertThat(command.getBuildId()).isEqualTo("666");
			
		}
		
	}
	
	@Test
	public void testSplitFeaturesTagExpression() {

		SplitFeatures command = new SplitFeatures();
		JCommander jcommander = JcommanderUtils.buildJcommander(null, Collections.singleton((Command) command));

		jcommander.parse("split-features", "--features", "features", "--tags", "@myTag and @otherTag", "-s", "eol");
		String commandName = jcommander.getParsedCommand();
		
		Command parsedCommand = JcommanderUtils.getCommandObject(jcommander, commandName);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			
			softly.assertThat(commandName).isEqualTo("split-features");
			softly.assertThat(parsedCommand).isEqualTo(command);
			softly.assertThat(command.getFeatures()).size().isEqualTo(1);
			softly.assertThat(command.getFeatures()).contains(Paths.get("features").toAbsolutePath());
			softly.assertThat(command.getOutput()).isEqualTo(SplitFeatures.DEFAULT_OUTPUT);
			softly.assertThat(command.getTagExpression()).isNotEqualTo(SplitFeatures.DEFAULT_TAG_EXPRESSION);
			softly.assertThat(command.getSeparator()).isEqualTo(Separator.EOL);
			softly.assertThat(command.getNumber()).isEqualTo(1);
			
		}
		
	}

}
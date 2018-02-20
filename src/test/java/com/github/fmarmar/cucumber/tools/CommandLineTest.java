package com.github.fmarmar.cucumber.tools;

import java.nio.file.Paths;
import java.util.Collections;

import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.junit.Test;

import com.beust.jcommander.JCommander;
import com.github.fmarmar.cucumber.tools.report.html.HtmlReport;

public class CommandLineTest {


	@Test
	public void testHtmlReportBasic() {

		HtmlReport command = new HtmlReport();
		JCommander jcommander = App.buildJcommander(null, Collections.singleton((Command) command));

		jcommander.parse("html-report", "--reports", "reports");
		
		Command parsedCommand = App.getCommand(jcommander);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			
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
		JCommander jcommander = App.buildJcommander(null, Collections.singleton((Command) command));

		jcommander.parse("html-report", "--reports", "reports", "--output", "target/", "--project", "test", "--build", "666");
		
		Command parsedCommand = App.getCommand(jcommander);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			
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
		JCommander jcommander = App.buildJcommander(null, Collections.singleton((Command) command));

		jcommander.parse("html-report", "-r", "reports", "-o", "target", "-p", "test", "-b", "666");
		
		Command parsedCommand = App.getCommand(jcommander);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			
			softly.assertThat(parsedCommand).isEqualTo(command);
			softly.assertThat(command.getReports()).size().isEqualTo(1);
			softly.assertThat(command.getReports()).contains(Paths.get("reports").toAbsolutePath());
			softly.assertThat(command.getOutput()).isEqualTo(Paths.get("target").toAbsolutePath());
			softly.assertThat(command.getProjectName()).isEqualTo("test");
			softly.assertThat(command.getBuildId()).isEqualTo("666");
			
		}
		
	}





}
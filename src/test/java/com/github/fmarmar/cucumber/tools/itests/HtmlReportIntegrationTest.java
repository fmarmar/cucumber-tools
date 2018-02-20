package com.github.fmarmar.cucumber.tools.itests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;

import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.junit.Test;

import com.github.fmarmar.cucumber.tools.TestUtils;
import com.github.fmarmar.cucumber.tools.report.html.HtmlReport;

public class HtmlReportIntegrationTest extends AbstractIntegrationTest {
	
	@Test
	public void testCreateReportWithDefaultOptions() throws IOException, InterruptedException {
		
		Path jsonReport = TestUtils.REPORTS_BASE_PATH.resolve("simple.json");
		
		Path workingDir = execute("html-report", "-r", jsonReport.toFile().getCanonicalPath());
		
		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			
			Path output = workingDir.resolve(HtmlReport.DEFAULT_OUTPUT);
			
			assertThat(output.toFile()).exists().isDirectory();
			assertThat(output.resolve("index.html").toFile()).exists().isFile();
			assertThat(output.resolve("features-overview.html").toFile()).exists().isFile();
			assertThat(output.resolve("failures-overview.html").toFile()).exists().isFile();
			
			assertThat(output.resolve("features").toFile()).exists().isDirectory();
			assertThat(output.resolve("embeddings").toFile()).exists().isDirectory();
			
		}
		
	}
	
	
	
	
}
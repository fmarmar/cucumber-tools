package com.github.fmarmar.cucumber.tools.itests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;

import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.junit.Test;

import com.github.fmarmar.cucumber.tools.TestUtils;
import com.github.fmarmar.cucumber.tools.split.SplitFeatures;

public class SplitFeaturesIntegrationTest extends AbstractIntegrationTest {
	
	@Test
	public void testSplitFeatureDefault() throws IOException, InterruptedException {
		
		Path workingDir = execute("split-features", "-f", TestUtils.SPLIT_FEATURES_BASE_PATH.toFile().getCanonicalPath());
		
		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			
			Path output = workingDir.resolve(SplitFeatures.DEFAULT_OUTPUT);
			
			assertThat(output.toFile()).exists().isDirectory();
			assertThat(output.resolve("scenarios.txt").toFile()).exists().isFile();
			assertThat(output.resolve("scenarios-001.txt").toFile()).doesNotExist();
			
		}
		
	}
	
	@Test
	public void testSplitFeatureInTwo() throws IOException, InterruptedException {
		
		Path workingDir = execute("split-features", "-f", TestUtils.SPLIT_FEATURES_BASE_PATH.toFile().getCanonicalPath(), "-n", "2");
		
		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			
			Path output = workingDir.resolve(SplitFeatures.DEFAULT_OUTPUT);
			
			assertThat(output.toFile()).exists().isDirectory();
			assertThat(output.resolve("scenarios-001.txt").toFile()).exists().isFile();
			assertThat(output.resolve("scenarios-002.txt").toFile()).exists().isFile();
			assertThat(output.resolve("scenarios-003.txt").toFile()).doesNotExist();
			
		}
		
	}
	
	@Test
	public void testSplitFeatureInThree() throws IOException, InterruptedException {
		
		Path workingDir = execute("split-features", "-f", TestUtils.SPLIT_FEATURES_BASE_PATH.toFile().getCanonicalPath(), "-n", "3");
		
		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			
			Path output = workingDir.resolve(SplitFeatures.DEFAULT_OUTPUT);
			
			assertThat(output.toFile()).exists().isDirectory();
			assertThat(output.resolve("scenarios-001.txt").toFile()).exists().isFile();
			assertThat(output.resolve("scenarios-002.txt").toFile()).exists().isFile();
			assertThat(output.resolve("scenarios-003.txt").toFile()).exists().isFile();
			
		}
		
	}
	
	
	
	
}
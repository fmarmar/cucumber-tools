package com.github.fmarmar.cucumber.tools;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class TestUtils {
	
	public static final Path CLI_JAR = Paths.get("target", "cucumber-tools-cli.jar");
	
	public static final Path BASE_TEST_RESOURCES = Paths.get("src", "test", "resources");
	
	private static final Path CLASSPATH_REPORTS_PATH = Paths.get("com", "github", "fmarmar", "cucumber", "tools", "report", "examples");
	
	public static final Path REPORTS_BASE_PATH = BASE_TEST_RESOURCES.resolve(CLASSPATH_REPORTS_PATH);
	
	public static final Path SPLIT_FEATURES_BASE_PATH = BASE_TEST_RESOURCES.resolve("split");
	
}
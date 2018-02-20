package com.github.fmarmar.cucumber.tools.itests;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.Timeout;

import com.github.fmarmar.cucumber.tools.TestUtils;
import com.google.common.base.Joiner;


public abstract class AbstractIntegrationTest {
	
	@Rule
    public Timeout globalTimeout = Timeout.seconds(60); // Integration tests should not take more than 60 seconds per test
	
	private String javaBin;
	
	@Before
	public void prepareTest() {
		
		javaBin = System.getProperty("java.home") +
				File.separator + "bin" +
				File.separator + "java";
		
	}
	
	protected Path execute(String... args) throws IOException, InterruptedException {
		
		Path workingDirectory = createTempDirectory("testWorkingDirectory");
		File logFile = workingDirectory.resolve("cli.log").toFile();
		
		List<String> command = buildCommand(args);
		
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		
		builder.redirectOutput(Redirect.appendTo(logFile));
		builder.directory(workingDirectory.toFile());
		
		Process process = builder.start();
		int exitCode = process.waitFor();
		
		if (exitCode != 0) {
			throw new ExecutionException(command, workingDirectory, exitCode);
		}
		
		return workingDirectory;
		
	}
	
	private List<String> buildCommand(String... args) throws IOException {
		
		List<String> command = new ArrayList<>(args.length + 5);
		
		command.add(javaBin);
		command.add("-jar");
		command.add(TestUtils.CLI_JAR.toFile().getCanonicalPath());
		
		command.addAll(Arrays.asList(args));
		
		return command;
	}
	
	protected Path createTempDirectory(String prefix) throws IOException {
		return Files.createTempDirectory( prefix.endsWith("-") ? prefix : (prefix + '-') );
	}
	
	public static class ExecutionException extends RuntimeException {
		
		private static final long serialVersionUID = 824246770546538845L;
		
		public final int exitCode;
		
		private ExecutionException(List<String> command, Path workingDirectory, int exitCode) {
			super("Commnad '" + Joiner.on(' ').join(command) + "' executed on " + workingDirectory + " failed with exitCode " + exitCode);
			this.exitCode = exitCode;
		}
		
	}
	
	
}
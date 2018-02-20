package com.github.fmarmar.cucumber.tools.report.html;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.fmarmar.cucumber.tools.Command;
import com.github.fmarmar.cucumber.tools.common.PathConverter;
import com.github.fmarmar.cucumber.tools.exception.CommandException;
import com.github.fmarmar.cucumber.tools.report.html.page.PageGenerator;
import com.github.fmarmar.cucumber.tools.report.html.page.velocity.VelocityPageGenerator;
import com.github.fmarmar.cucumber.tools.report.html.support.ReportMetadata;
import com.github.fmarmar.cucumber.tools.report.parser.ParsedReports;
import com.github.fmarmar.cucumber.tools.report.parser.ReportParser;

import lombok.Getter;

@Parameters(
		commandNames = "html-report", 
		commandDescription = "Generate an html report from the given json reports"
		)
public class HtmlReport implements Command {

	public static final int DEFAULT_THREADS_SIZE = 100;

	public static final Path DEFAULT_OUTPUT = Paths.get("reports", "html");
	
	public static final String DEFAULT_PROJECT_NAME = "No project";

	@Getter
	@Parameter(
			names = { "--reports", "-r" }, 
			variableArity = true, 
			description = "Path(s) where to find the json reports", 
			converter = PathConverter.class)
	private List<Path> reports;

	@Getter
	@Parameter(
			names = { "--output", "-o" }, 
			description = "Path where the report will be generated. Default: reports/html", 
			converter = PathConverter.class)
	private Path output = DEFAULT_OUTPUT;
	
	@Getter
	@Parameter(
			names = { "--project", "-p" }, 
			description = "Project name to print in the report. Default: " + DEFAULT_PROJECT_NAME)
	private String projectName = DEFAULT_PROJECT_NAME;
	
	@Getter
	@Parameter(
			names = { "--build", "-b" }, 
			description = "Build id to print in the report. Default: (empty)")
	private String buildId = null;

	private ReportParser parser;

	private ReportGenerator generator;

	@Override
	public void initialize() {
		checkOutput();

		try {
			init();
		} catch (IOException e) {
			throw new CommandException(e.getMessage(), e);
		}
	}

	private void checkOutput() {

		File outputFile = output.toFile();

		if (outputFile.exists() && !outputFile.isDirectory()) {
			throw new IllegalArgumentException(output + " is not a directory");
		}

		try {
			Files.createDirectories(output);
		} catch (IOException e) {
			throw new CommandException(e.getMessage(), e);
		}

	}

	private void init() throws IOException {
		parser = new ReportParser();
		ReportMetadata reportMetadata = new ReportMetadata(projectName, buildId);
		PageGenerator pageGenerator = new VelocityPageGenerator(reportMetadata, DEFAULT_THREADS_SIZE);
		generator = new ReportGenerator(pageGenerator, output, DEFAULT_THREADS_SIZE);
	}

	@Override
	public void run() {

		try {
			ParsedReports parsedReports = parser.parse(reports);

			generator.prepareReport(parsedReports.getEmbeddingsDirectory());
			generator.generateReport(parsedReports.getFeatures());
			generator.finishReport();

		} catch (IOException | InterruptedException e) {
			throw new CommandException(e.getMessage(), e);
		}
	}

}
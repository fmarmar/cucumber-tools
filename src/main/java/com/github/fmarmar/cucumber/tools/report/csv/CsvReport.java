package com.github.fmarmar.cucumber.tools.report.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.fmarmar.cucumber.tools.Command;
import com.github.fmarmar.cucumber.tools.exception.CommandException;
import com.github.fmarmar.cucumber.tools.jcommander.PathConverter;
import com.github.fmarmar.cucumber.tools.report.parser.ParsedReports;
import com.github.fmarmar.cucumber.tools.report.parser.ReportParser;

import lombok.Getter;

@Parameters(
		commandNames = "csv-report",
		commandDescription = "Generate an csv report from the given json reports"
		)
public class CsvReport implements Command {

	public enum ReportType { FEATURES }

	public static final Path DEFAULT_OUTPUT = Paths.get("reports");

	public static final String DEFAULT_NAME = "report";


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
			names = { "--name", "-n" },
			description = "Report file name (without the extension). Default: " + DEFAULT_NAME)
	private String name = DEFAULT_NAME;

	@Getter
	@Parameter(
			names = { "--type", "-t" },
			description = "Report type (features) (Default: features)")
	private ReportType type = ReportType.FEATURES;

	private Path outputFile;

	private ReportParser parser;

	private Reporter reporter;

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

		if (Files.exists(output) && !Files.isDirectory(output)) {
			throw new IllegalArgumentException(output + " is not a directory");
		}

		try {
			Files.createDirectories(output);
		} catch (IOException e) {
			throw new CommandException(e.getMessage(), e);
		}

		outputFile = output.resolve(name + ".csv");

	}

	private void init() throws IOException {
		parser = new ReportParser();
		reporter = new Reporter(outputFile, type);
	}

	@Override
	public void run() {

		try {
			ParsedReports parsedReports = parser.parse(reports);

			reporter.generateReport(parsedReports.getFeatures());

		} catch (IOException e) {
			throw new CommandException(e.getMessage(), e);
		}
	}



}
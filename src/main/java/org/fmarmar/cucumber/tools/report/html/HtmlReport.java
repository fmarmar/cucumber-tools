package org.fmarmar.cucumber.tools.report.html;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.fmarmar.cucumber.tools.Command;
import org.fmarmar.cucumber.tools.common.PathConverter;
import org.fmarmar.cucumber.tools.exception.CommandException;
import org.fmarmar.cucumber.tools.report.ReportParser;
import org.fmarmar.cucumber.tools.report.html.page.PageGenerator;
import org.fmarmar.cucumber.tools.report.html.page.velocity.VelocityPageGenerator;
import org.fmarmar.cucumber.tools.report.model.Feature;
import org.fmarmar.cucumber.tools.rerun.RerunFileConverter;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandNames = "html-report", commandDescription = "")
public class HtmlReport implements Command {

	public static final int DEFAULT_THREADS_SIZE = 100;

	public static final Path DEFAULT_OUTPUT = Paths.get("reports", "html");
	
	@Parameter(names = { "--reports", "-r" }, variableArity = true, description = "", converter = PathConverter.class)
	private List<Path> reports;
	
	@Parameter(names = { "--output", "-o" }, description = "", converter = RerunFileConverter.class)
	private Path output = DEFAULT_OUTPUT;
	
	private ReportParser parser;
	
	private ReportGenerator generator;
	
	@Override
	public void initialize() {
		checkOutput();
		init();
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
	
	private void init() {
		parser = new ReportParser();
		PageGenerator pageGenerator = new VelocityPageGenerator(DEFAULT_THREADS_SIZE);
		generator = new ReportGenerator(pageGenerator, output, DEFAULT_THREADS_SIZE);
	}

	@Override
	public void run() {
		
		try {
			List<Feature> features = parser.parse(reports);
			
			generator.prepareReport();
			generator.generateReport(features);
			generator.finishReport();
			
		} catch (IOException e) {
			throw new CommandException(e.getMessage(), e);
		}
	}
	
	

	
	
	
	
}
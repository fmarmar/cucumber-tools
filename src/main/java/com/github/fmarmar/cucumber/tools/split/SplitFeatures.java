package com.github.fmarmar.cucumber.tools.split;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.fmarmar.cucumber.tools.Command;
import com.github.fmarmar.cucumber.tools.exception.CommandException;
import com.github.fmarmar.cucumber.tools.jcommander.PathConverter;
import com.github.fmarmar.cucumber.tools.jcommander.TagExpressionConverter;
import com.github.fmarmar.cucumber.tools.split.SplitterByScenario.PickleInfo;
import com.google.common.collect.Lists;

import io.cucumber.tagexpressions.Expression;
import lombok.Getter;

/**
 * 
 * https://relishapp.com/cucumber/cucumber/docs/formatters/rerun-formatter
 * 
 * @author fmarmar
 *
 */
@Parameters(commandNames = "split-features", commandDescription = "Split the given features")
public class SplitFeatures implements Command {

	public enum Separator { SPACE, EOL }

	public static final Expression DEFAULT_TAG_EXPRESSION = TagExpressionConverter.NO_EXPRESSION;

	public static final Path DEFAULT_OUTPUT = Paths.get("target");

	@Getter
	@Parameter(
			names = { "--features", "-f" }, 
			variableArity = true, 
			description = "Path to feature(s)", 
			converter = PathConverter.class)
	private List<Path> features;

	@Getter
	@Parameter(
			names = { "--tags", "-t" }, 
			description = "Tag expression to filter", 
			converter = TagExpressionConverter.class)
	private Expression tagExpression = TagExpressionConverter.NO_EXPRESSION;

	@Getter
	@Parameter(
			names = { "--output", "-o" }, 
			description = "Directory where output will be written (DEFAULT: target)", 
			converter = PathConverter.class)
	private Path output = DEFAULT_OUTPUT;

	@Getter
	@Parameter(
			names = { "--separator", "-s" }, 
			description = "Separator character to use (eol|space) (DEFAULT: space)")
	private Separator separator = Separator.SPACE;

	@Getter
	@Parameter(
			names = { "--number", "-n" }, 
			description = "Number of files to create (DEFAULT: 1)")
	private int number = 1;

	private char separatorChar;

	@Override
	public void initialize() {
		checkOutput();
		separatorChar = separatorChar(separator);
	}

	private void checkOutput() {

		if (Files.isRegularFile(output)) {
			throw new IllegalArgumentException(output + " is not a directory");
		}

		try {
			Files.createDirectories(output);
		} catch (IOException e) {
			throw new CommandException(e.getMessage(), e);
		}

	}

	@Override
	public void run() {

		try {

			SplitterByScenario splitter = new SplitterByScenario();

			List<PickleInfo> pickles = splitter.split(features, tagExpression);

			if (number == 1) {
				generateOutput(output.resolve("scenarios.txt"), pickles);
			} else {
				List<List<PickleInfo>> partitions = Lists.partition(pickles, (int) Math.ceil(pickles.size() / number));

				int idx = 1;
				for (List<PickleInfo> partition : partitions) {
					generateOutput(partition, idx++);
				}
			}

		} catch (IOException e) {
			throw new CommandException(e.getMessage(), e);
		}

	}

	private void generateOutput(List<PickleInfo> pickles, int partition) throws IOException {

		Path outputFile = output.resolve(String.format("scenarios-%03d.txt", partition));
		generateOutput(outputFile, pickles);

	}

	private void generateOutput(Path outputFile, List<PickleInfo> pickles) throws IOException {

		try (Writer writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {

			for (PickleInfo pickle : pickles) {
				pickle.appendTo(writer);
				writer.append(separatorChar);
			}

		}

	}



	private static char separatorChar(Separator sep) {
		switch(sep) {
			case SPACE:
				return ' ';
			case EOL:
				return '\n';
			default:
				throw new IllegalArgumentException("Unknown separator value: " + sep);
		}
	}

}
package com.github.fmarmar.cucumber.tools.rerun;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.fmarmar.cucumber.tools.Command;
import com.github.fmarmar.cucumber.tools.common.PathConverter;
import com.github.fmarmar.cucumber.tools.exception.CommandException;
import com.google.common.collect.Iterables;

import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.ParserException;
import gherkin.ast.GherkinDocument;
import gherkin.pickles.Compiler;
import gherkin.pickles.Pickle;
import gherkin.pickles.PickleLocation;
import gherkin.pickles.PickleTag;
import io.cucumber.tagexpressions.Expression;

/**
 * 
 * https://relishapp.com/cucumber/cucumber/docs/formatters/rerun-formatter
 * 
 * @author fmarmar
 *
 */
@Parameters(commandNames = "rerun", commandDescription = "")
public class Rerun implements Command {

	private static final Path DEFAULT_OUTPUT = Paths.get("target", RerunFileConverter.DEFAULT_FILE_NAME);

	@Parameter(names = { "--tags", "-t" }, description = "", converter = TagExpressionConverter.class)
	private Expression tagExpression = TagExpressionConverter.NO_EXPRESSION;

	@Parameter(names = { "--features", "-f" }, variableArity = true, description = "", converter = PathConverter.class)
	private List<Path> features;

	@Parameter(names = { "--output", "-o" }, description = "", converter = RerunFileConverter.class)
	private Path output = DEFAULT_OUTPUT;

	private Parser<GherkinDocument> gherkinParser;

	private Compiler gherkinCompiler;

	@Override
	public void initialize() {

		checkOutput();
		buildGherkinTools();

	}

	private void checkOutput() {

		File outputFile = output.toFile();

		if (outputFile.exists() && outputFile.isDirectory()) {
			output = output.resolve(RerunFileConverter.DEFAULT_FILE_NAME);
		}

		try {
			Files.createDirectories(output.getParent());
		} catch (IOException e) {
			throw new CommandException(e.getMessage(), e);
		}

	}

	private void buildGherkinTools() {
		gherkinParser = new Parser<>(new AstBuilder());
		gherkinParser.stopAtFirstError = true;
		gherkinCompiler = new Compiler();
	}

	@Override
	public void run() {

		try {
			Iterable<FeaturePickles> pickles = Collections.emptyList();

			for (Path feature : features) {
				pickles = Iterables.concat(pickles, picklesForFeature(feature));
			}

			generateOutput(pickles);

		} catch (IOException e) {
			throw new CommandException(e.getMessage(), e);
		}

	}

	private Collection<FeaturePickles> picklesForFeature(Path feature) throws IOException {

		File featureFile = feature.toFile();

		if (featureFile.exists()) {

			if (featureFile.isDirectory()) {
				return scanDirectory(feature);
			}

			if (isFeatureFile(feature)) {
				return Collections.singletonList(parseFeature(feature));
			}

		}

		return Collections.emptyList();
	}

	private Collection<FeaturePickles> scanDirectory(Path dir) throws IOException {

		final Collection<FeaturePickles> pickles = new ArrayList<>();

		Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

				if (attrs.isRegularFile() && isFeatureFile(file)) {
					pickles.add(parseFeature(file));
				}

				return FileVisitResult.CONTINUE;
			}

		});

		return pickles;
	}

	private boolean isFeatureFile(Path feature) {
		return feature.getFileName().toString().endsWith(".feature");
	}

	private FeaturePickles parseFeature(Path feature) throws IOException {

		try (Reader reader = newBufferedReader(feature)) {
			GherkinDocument document = gherkinParser.parse(reader);
			return new FeaturePickles(feature, gherkinCompiler.compile(document));
		} catch (ParserException e) {
			throw new CommandException("Error parsing feature: " + feature, e);
		}

	}

	private static Reader newBufferedReader(Path feature) throws IOException {
		return new BufferedReader(new InputStreamReader(new BOMInputStream(Files.newInputStream(feature))));
	}

	private void generateOutput(Iterable<FeaturePickles> pickles) throws IOException {

		try (Writer writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {

			for (FeaturePickles featurePickles : pickles) {
				for (Pickle pickle : featurePickles.pickles) {

					if (tagExpression.evaluate(tagList(pickle.getTags()))) {

						writer.append(normalize(featurePickles.feature));

						for (PickleLocation location : pickle.getLocations()) {
							writer.append(':').append(String.valueOf(location.getLine()));
						}

						writer.append(' ');
					}

				}
			}

		}

	}

	private static List<String> tagList(List<PickleTag> pickleTags) {

		List<String> tags = new ArrayList<>(pickleTags.size());

		for (PickleTag pickleTag : pickleTags) {
			tags.add(pickleTag.getName());
		}

		return tags;
	}

	private static String normalize(Path path) {
		if (System.getProperty("file.separator").equals("\\")) {
			return path.toString().replaceAll("\\\\+", "/");
		} else {
			return path.toString();
		}
	}

	private static class FeaturePickles {

		private final Path feature;

		private Collection<Pickle> pickles;

		private FeaturePickles(Path feature, Collection<Pickle> pickles) {
			this.feature = feature;
			this.pickles = pickles;

		}
	}
}
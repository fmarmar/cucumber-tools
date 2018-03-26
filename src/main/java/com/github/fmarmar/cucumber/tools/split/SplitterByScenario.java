package com.github.fmarmar.cucumber.tools.split;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;

import com.github.fmarmar.cucumber.tools.exception.CommandException;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.ParserException;
import gherkin.ast.GherkinDocument;
import gherkin.pickles.Compiler;
import gherkin.pickles.Pickle;
import gherkin.pickles.PickleLocation;
import gherkin.pickles.PickleTag;
import io.cucumber.tagexpressions.Expression;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SplitterByScenario {
	
	private final Parser<GherkinDocument> gherkinParser;

	private final Compiler gherkinCompiler;
	
	public SplitterByScenario() {
		gherkinParser = new Parser<>(new AstBuilder());
		gherkinParser.stopAtFirstError = true;
		gherkinCompiler = new Compiler();
	}
	
	public List<PickleInfo> split(Path feature, final Expression tagExpression) throws IOException {
		return split(Collections.singletonList(feature), tagExpression);
	}
	
	public List<PickleInfo> split(Collection<Path> features, final Expression tagExpression) throws IOException {
		
		Collection<PickleInfo> pickles = new ArrayList<>();
		
		for (Path feature : features) {
			pickles.addAll(picklesFor(feature));
		}
		
		return new ArrayList<>(Collections2.filter(pickles, new Predicate<PickleInfo>() {

			@Override
			public boolean apply(PickleInfo input) {
				return tagExpression.evaluate(input.tags);
			}
		}));
		
	}


	private Collection<PickleInfo> picklesFor(Path feature) throws IOException {

		if (Files.exists(feature)) {
			
			if (Files.isDirectory(feature)) {
				return scanDirectory(feature);
			}
			
			if (isFeatureFile(feature)) {
				return parseFeature(feature);
			}

			
		}
		
		log.warn("Can't extract scenarios from {}", feature);
		return Collections.emptyList();
	}


	private Collection<PickleInfo> scanDirectory(Path dir) throws IOException {
		
		final Collection<PickleInfo> pickles = new ArrayList<>();

		Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

				if (attrs.isRegularFile() && isFeatureFile(file)) {
					pickles.addAll(parseFeature(file));
				}

				return FileVisitResult.CONTINUE;
			}

		});

		return pickles;
	}
		
	private boolean isFeatureFile(Path feature) {
		return feature.getFileName().toString().endsWith(".feature");
	}
	
	private Collection<PickleInfo> parseFeature(final Path feature) throws IOException {

		try (Reader reader = newBufferedReader(feature)) {
			GherkinDocument document = gherkinParser.parse(reader);
			List<Pickle> pickles = gherkinCompiler.compile(document);
					
			return Collections2.transform(pickles, new Function<Pickle, PickleInfo>() {

				@Override
				public PickleInfo apply(Pickle input) {
					return new PickleInfo(feature, input);
				}
			});
			
		} catch (ParserException e) {
			throw new CommandException("Error parsing feature: " + feature, e);
		}

	}

	private static Reader newBufferedReader(Path feature) throws IOException {
		return new BufferedReader(new InputStreamReader(new BOMInputStream(Files.newInputStream(feature))));
	}

	public static class PickleInfo {
		
		private final Path feature;
		private final List<String> locations;
		private final List<String> tags;

		private PickleInfo(Path feature, Pickle pickle) {
			this.feature = feature;
			
			this.locations = Lists.transform(pickle.getLocations(), new Function<PickleLocation, String>() {
				@Override
				public String apply(PickleLocation input) {
					return String.valueOf(input.getLine());
				}
			});
			
			this.tags = Lists.transform(pickle.getTags(), new Function<PickleTag, String>() {
				@Override
				public String apply(PickleTag input) {
					return input.getName();
				}
			});
		}
		
		public void appendTo(Appendable appendable) throws IOException {
			appendable.append(normalize(feature));
			
			for (String location : locations) {
				appendable.append(':').append(location);
			}
		}
		
	}
	
	private static String normalize(Path path) {
		if (System.getProperty("file.separator").equals("\\")) {
			return path.toString().replaceAll("\\\\+", "/");
		} else {
			return path.toString();
		}
	}
	
}
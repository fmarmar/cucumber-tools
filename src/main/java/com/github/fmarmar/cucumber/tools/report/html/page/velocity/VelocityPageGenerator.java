package com.github.fmarmar.cucumber.tools.report.html.page.velocity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeConstants.SpaceGobbling;

import com.github.fmarmar.cucumber.tools.exception.MultiException;
import com.github.fmarmar.cucumber.tools.report.html.page.PageGenerator;
import com.github.fmarmar.cucumber.tools.report.html.support.ReportMetadata;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchProcessor;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.FilenameMatchProcessor;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import lombok.AllArgsConstructor;

public class VelocityPageGenerator implements PageGenerator {
	
	private static final String BASE_RESOURCES = "report/html/DEFAULT/";

	private static final String TEMPLATES_DIR = "templates/";
	
	private static final String MACROS_DIR = "macros/";
	
	private static final String STATIC_DIR = "static/";
	
	private static final String PAGE_SUFFIX = ".html";
	
	private static final String TEMPLATE_EXTENSION = "vm";
	
	private static final Map<PageId, PageIdInfo> PAGE_ID_MAP = new ImmutableMap.Builder<PageId, PageIdInfo>()
			.put(PageId.FEATURES_OVERVIEW, new PageIdInfo("featuresOverview.vm", ".", null, "features-overview"))
			.put(PageId.FEATURE, new PageIdInfo("feature.vm", "..", "features", null))
			.put(PageId.TAGS_OVERVIEW, new PageIdInfo("tagOverview.vm", ".", null, "tags-overview"))
			.put(PageId.FAILURES_OVERVIEW, new PageIdInfo("failuresOverview.vm", ".", null, "failures-overview"))
			.build();

	private final VelocityEngine engine;

	private final VelocityContext globalContext;
	
	private final String baseResources = BASE_RESOURCES;
	
	public VelocityPageGenerator(ReportMetadata reportMetadata, int parserPoolSize) throws IOException {

		engine = new VelocityEngine();
		engine.init(engineProperties(baseResources + TEMPLATES_DIR, parserPoolSize));

		globalContext = buildGlobalContext(reportMetadata);	

	}

	private Properties engineProperties(String baseTemplates, int parserPoolSize) throws IOException {

		Properties veProps = new Properties();
		
		// 1.x compatibility
		veProps.setProperty("runtime.conversion.handler", "none");
		veProps.setProperty(RuntimeConstants.SPACE_GOBBLING, SpaceGobbling.BC.name());
		veProps.setProperty(RuntimeConstants.CHECK_EMPTY_OBJECTS, Boolean.FALSE.toString());

		veProps.setProperty(RuntimeConstants.PARSER_POOL_SIZE, String.valueOf(parserPoolSize));

		// Resource Loaders
		veProps.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		veProps.setProperty("classpath.resource.loader.class", BaseClasspathResourceLoader.class.getCanonicalName());
		veProps.setProperty("classpath.resource.loader.base", baseTemplates);

		// Encoding
		veProps.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
		
		veProps.setProperty(RuntimeConstants.VM_LIBRARY, Joiner.on(',').join(getMacros(baseTemplates)));

		return veProps;
	}
	
	private Collection<String> getMacros(final String baseTemplates) throws IOException {
		
		final Collection<String> macros = new ArrayList<>();
		
		new FastClasspathScanner(toPackage(baseTemplates + MACROS_DIR))
				.matchFilenameExtension(TEMPLATE_EXTENSION, new FilenameMatchProcessor() {

					@Override
					public void processMatch(File classpathElt, String relativePath) throws IOException {
						macros.add(relativePath.substring(baseTemplates.length()));
					}
					
				})
				.scan();
		
		return macros;
	}

	private VelocityContext buildGlobalContext(ReportMetadata metadata) {

		VelocityContext context = new VelocityContext();

		// to escape html and xml
		EventCartridge ec = new EventCartridge();
		ec.addEventHandler(new EscapeHtmlReference());
		context.attachEventCartridge(ec);

		context.put("utils", TemplateUtils.INSTANCE);

		context.put("metadata", metadata);

		return context;

	}
	
	@Override
	public void initialize(Path output) throws IOException {
		
		for (PageIdInfo pageIdInfo : PAGE_ID_MAP.values()) {
			if (pageIdInfo.subDirectory != null) {
				Files.createDirectories(output.resolve(pageIdInfo.subDirectory));
			}
		}
		
		Files.createDirectories(output.resolve("embeddings"));
	}

	@Override
	public void copyStaticResources(final Path output) throws IOException {
		
		final String baseStatic = baseResources + STATIC_DIR;
		
		ScanResult scanResult = new FastClasspathScanner(toPackage(baseStatic))
				.matchFilenamePattern(".*", new FileMatchProcessor() {

					@Override
					public void processMatch(String relativePath, InputStream is, long lengthBytes) throws IOException {

						Path outputPath = output.resolve(relativePath.substring(baseStatic.length()));
						Files.createDirectories(outputPath.getParent());
						
						try (OutputStream os = Files.newOutputStream(outputPath)) {
							ByteStreams.copy(is, os);
						}
						
					}
					
				})
				.scan();
		
		List<Throwable> errors = scanResult.getMatchProcessorExceptions();
		
		if (!errors.isEmpty()) {
			throw MultiException.newInstance("Error copying static resources", errors);
		}
		
	}
	
	private static String toPackage(String path) {
		String pkg = path.replaceAll("/+", ".");
		return StringUtils.stripEnd(pkg, ".");
	}
	
	@Override
	public void copyEmbeddings(Path embeddingsDirectory, final Path output) throws IOException {
		
		Files.walkFileTree(embeddingsDirectory, EnumSet.noneOf(FileVisitOption.class), 1, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

				if (attrs.isRegularFile()) {
					Files.copy(file, output.resolve("embeddings").resolve(file.getFileName()));
				}

				return FileVisitResult.CONTINUE;
			}

		});
		
		
	}
	
	@Override
	public Path resolvePagePath(PageId pageId) {
		
		if (PAGE_ID_MAP.containsKey(pageId)) {
			String name = PAGE_ID_MAP.get(pageId).name;
			
			if (name != null) {
				return resolvePagePath(pageId, name);
			}
		}
		
		throw new IllegalArgumentException("Can't resolve page path for: " + pageId);
		
	}
	
	@Override
	public Path resolvePagePath(PageId pageId, String name) {
		
		String finalName = name + PAGE_SUFFIX;
		
		if (PAGE_ID_MAP.containsKey(pageId)) {
			String subDirectory = PAGE_ID_MAP.get(pageId).subDirectory;
			
			if (subDirectory == null) {
				return Paths.get(finalName);
			}
			
			return Paths.get(subDirectory, finalName);
		}
		
		throw new IllegalArgumentException("Unknown pageId: " + pageId);
		
	}
	
	@Override
	public void generatePage(PageId pageId, Path page, Map<String, Object> model) throws IOException {

		VelocityContext pageContext = buildPageContext(pageId, model);
		writePage(templateLocation(pageId), page, pageContext);
	}
	
	private String templateLocation(PageId pageId) {
		
		if (PAGE_ID_MAP.containsKey(pageId)) {
			return PAGE_ID_MAP.get(pageId).templateName;
		}
		
		throw new IllegalArgumentException("Unknown pageId: " + pageId);
		
	}

	private VelocityContext buildPageContext(PageId pageId, Map<String, Object> model) {

		VelocityContext context = new VelocityContext(globalContext);

		// to provide unique ids for elements on each page
		context.put("counter", new Counter());
		
		context.put("basePath", basePathForPage(pageId));

		for (Entry<String, Object> modelEntry : model.entrySet()) {
			context.put(modelEntry.getKey(), modelEntry.getValue());
		}

		return context;

	}

	private String basePathForPage(PageId pageId) {
		
		if (PAGE_ID_MAP.containsKey(pageId)) {
			return PAGE_ID_MAP.get(pageId).basePath;
		}
		
		throw new IllegalArgumentException("Unknown pageId: " + pageId);
		
	}

	private void writePage(String templateLocation, Path page, VelocityContext pageContext) throws IOException {

		Template template = engine.getTemplate(templateLocation);

		try (Writer writer = Files.newBufferedWriter(page, StandardCharsets.UTF_8)) {
			template.merge(pageContext, writer);
		}

	}
	
	@AllArgsConstructor
	private static class PageIdInfo {
		
		private final String templateName;
		private final String basePath;
		private final String subDirectory;
		private final String name;
		
	}

}
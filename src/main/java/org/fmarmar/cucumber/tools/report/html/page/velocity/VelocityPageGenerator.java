package org.fmarmar.cucumber.tools.report.html.page.velocity;

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
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.runtime.RuntimeConstants;
import org.fmarmar.cucumber.tools.report.html.page.PageGenerator;
import org.fmarmar.cucumber.tools.report.html.support.ReportMetadata;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;

import lombok.AllArgsConstructor;

public class VelocityPageGenerator implements PageGenerator {
	
	private static final String BASE_RESOURCES = "report/html/DEFAULT/";

	private static final String BASE_TEMPLATES = BASE_RESOURCES + "templates/";

	private static final String BASE_STATIC = BASE_RESOURCES + "static/";
	
	private static final String PAGE_SUFFIX = ".html";
	
	private static final Map<PageId, PageIdInfo> PAGE_ID_MAP = new ImmutableMap.Builder<PageId, PageIdInfo>()
			.put(PageId.FEATURES_OVERVIEW, new PageIdInfo("featuresOverview.vm", ".", null, "features-overview"))
			.put(PageId.FEATURE, new PageIdInfo("feature.vm", "..", "features", null))
			.put(PageId.TAGS_OVERVIEW, new PageIdInfo("tagOverview.vm", ".", null, "tags-overview"))
			.put(PageId.FAILURES_OVERVIEW, new PageIdInfo("failuresOverview.vm", ".", null, "failures-overview"))
			.build();
			

	private static final String[] MACROS = {
			"macros/js/arrays.js.vm",
			"macros/page/head.vm",
			"macros/page/title.vm",
			"macros/page/navigation.vm",
			"macros/page/buildinfo.vm",
			"macros/page/classifications.vm",
			"macros/page/reportInfo.vm",
			"macros/page/lead.vm",
			"macros/report/statsTable.vm",
			"macros/report/reportTable.vm",
			"macros/report/expandAllButtons.vm",
			"macros/section/duration.vm",
			"macros/section/brief.vm",
			"macros/section/tags.vm",
			"macros/section/message.vm",
			"macros/section/output.vm",
			"macros/section/embeddings.vm",
			"macros/section/hooks.vm",
			"macros/section/docstring.vm",
			"macros/section/steps.vm",
			"macros/section/scenario.vm"
	};
	
	private static final String[] STATIC_RESOURCES = {
			"css/bootstrap.min.css",
			"css/cucumber.css",
			"css/font-awesome.min.css",
			"fonts/fontawesome-webfont.eot",
			"fonts/fontawesome-webfont.svg",
			"fonts/fontawesome-webfont.ttf",
			"fonts/fontawesome-webfont.woff",
			"fonts/fontawesome-webfont.woff2",
			"fonts/FontAwesome.otf",
			"fonts/glyphicons-halflings-regular.eot",
			"fonts/glyphicons-halflings-regular.svg",
			"fonts/glyphicons-halflings-regular.ttf",
			"fonts/glyphicons-halflings-regular.woff",
			"fonts/glyphicons-halflings-regular.woff2",
			"images/favicon.png",
			"js/bootstrap.min.js",
			"js/Chart.min.js",
			"js/jquery.min.js",
			"js/jquery.tablesorter.min.js",
			"js/moment.min.js",
			"index.html"
	};

	private final VelocityEngine engine;

	private final VelocityContext globalContext;

	public VelocityPageGenerator(ReportMetadata reportMetadata, int parserPoolSize) {

		engine = new VelocityEngine();
		engine.init(engineProperties(BASE_TEMPLATES, parserPoolSize));

		globalContext = buildGlobalContext(reportMetadata);	

	}

	private Properties engineProperties(String baseTemplates, int parserPoolSize) {

		Properties veProps = new Properties();

		veProps.setProperty(RuntimeConstants.PARSER_POOL_SIZE, String.valueOf(parserPoolSize));

		// Resource Loaders
		veProps.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		veProps.setProperty("classpath.resource.loader.class", BaseClasspathResourceLoader.class.getCanonicalName());
		veProps.setProperty("classpath.resource.loader.base", baseTemplates);

		// Encoding
		veProps.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
		veProps.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");

		veProps.setProperty(RuntimeConstants.VM_LIBRARY, Joiner.on(',').join(MACROS));

		return veProps;
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
	public void copyStaticResources(Path output) throws IOException {
		
		// FIXME make generic
		
		for (String resource : STATIC_RESOURCES) {
			copyClasspathResource(BASE_STATIC + resource, output.resolve(resource));
		}
		
	}
	
	private void copyClasspathResource(String resourceLocation, Path outputPath) throws IOException {
		
		Files.createDirectories(outputPath.getParent());
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		try (InputStream is = cl.getResourceAsStream(resourceLocation); OutputStream os = Files.newOutputStream(outputPath)) {
			if (is == null) {
				throw new IllegalArgumentException("Resource " + resourceLocation + " not found in classpath");
			}
			ByteStreams.copy(is, os);
		}
		
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
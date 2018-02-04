package org.fmarmar.cucumber.tools.report.html;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.runtime.RuntimeConstants;
import org.fmarmar.cucumber.tools.report.html.support.BaseClasspathResourceLoader;
import org.fmarmar.cucumber.tools.report.html.support.Counter;
import org.fmarmar.cucumber.tools.report.html.support.EscapeHtmlReference;
import org.fmarmar.cucumber.tools.report.html.support.ReportMetadata;
import org.fmarmar.cucumber.tools.report.html.support.Util;

import com.google.common.base.Joiner;

public class VelocityPageGenerator {

	private static final String[] MACROS = {
			"macros/js/arrays.js.vm",
			"macros/page/head.vm",
			"macros/page/title.vm",
			"macros/page/navigation.vm",
			"macros/page/buildinfo.vm",
			"macros/page/classifications.vm",
			"macros/page/reportInfo.vm",
			"macros/page/lead.vm",
			"macros/report/reportHeader.vm",
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


	private final VelocityEngine engine;

	private VelocityContext globalContext;

	public VelocityPageGenerator(String baseTemplates, int poolSize) {

		engine = new VelocityEngine();
		engine.init(engineProperties(baseTemplates, poolSize));

		globalContext = buildGlobalContext(new ReportMetadata());	

	}

	private Properties engineProperties(String baseTemplates, int poolSize) {

		Properties veProps = new Properties();

		veProps.setProperty(RuntimeConstants.PARSER_POOL_SIZE, String.valueOf(poolSize));

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

		context.put("util", Util.INSTANCE);

		context.put("metadata", metadata);

		return context;

	}

	public void generatePage(String templateLocation, Path page, Map<String, Object> model) throws IOException {

		VelocityContext pageContext = buildPageContext(model);
		writePage(templateLocation, page, pageContext);
	}

	private VelocityContext buildPageContext(Map<String, Object> model) {

		VelocityContext context = new VelocityContext(globalContext);

		// to provide unique ids for elements on each page
		context.put("counter", new Counter());

		for (Entry<String, Object> modelEntry : model.entrySet()) {
			context.put(modelEntry.getKey(), modelEntry.getValue());
		}

		return context;

	}

	private void writePage(String templateLocation, Path page, VelocityContext pageContext) throws IOException {

		Template template = engine.getTemplate(templateLocation);

		try (Writer writer = Files.newBufferedWriter(page, StandardCharsets.UTF_8)) {
			template.merge(pageContext, writer);
		}

	}

}
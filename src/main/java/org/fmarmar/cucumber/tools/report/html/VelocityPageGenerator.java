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
import org.fmarmar.cucumber.tools.report.html.support.BaseClasspathResourceLoader;
import org.fmarmar.cucumber.tools.report.html.support.Counter;
import org.fmarmar.cucumber.tools.report.html.support.EscapeHtmlReference;
import org.fmarmar.cucumber.tools.report.html.support.ReportMetadata;
import org.fmarmar.cucumber.tools.report.html.support.Util;



public class VelocityPageGenerator {
	
	private final VelocityEngine engine;
	
	private VelocityContext globalContext;
	
	public VelocityPageGenerator(String baseTemplates, Path output) {
		
		engine = new VelocityEngine();
		engine.init(engineProperties(baseTemplates));
		
		globalContext = buildGlobalContext(new ReportMetadata());	
		
	}

	private Properties engineProperties(String baseTemplates) {
		
		Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class", BaseClasspathResourceLoader.class.getCanonicalName());
        props.setProperty("class.resource.loader.base", baseTemplates);
        
        return props;
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
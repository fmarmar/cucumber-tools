package org.fmarmar.cucumber.tools.report.html.support;

import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class BaseClasspathResourceLoader extends ClasspathResourceLoader {
	
	private String base = StringUtils.EMPTY;
	
	public void init(ExtendedProperties configuration) {
		
		super.init(configuration);
		
		base = configuration.getString("base", StringUtils.EMPTY);
		
	}
	
	public InputStream getResourceStream(String name) throws ResourceNotFoundException {
		return super.getResourceStream(base + name);
	}
	
}
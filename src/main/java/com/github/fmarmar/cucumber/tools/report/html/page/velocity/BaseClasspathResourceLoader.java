package com.github.fmarmar.cucumber.tools.report.html.page.velocity;

import java.io.Reader;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.util.ExtProperties;

public class BaseClasspathResourceLoader extends ClasspathResourceLoader {

	private String base = StringUtils.EMPTY;

	@Override
	public void init(ExtProperties configuration) {

		super.init(configuration);

		base = configuration.getString("base", StringUtils.EMPTY);

	}

	@Override
	public Reader getResourceReader(String name, String encoding) throws ResourceNotFoundException {
		return super.getResourceReader(base + name, encoding);
	}

}
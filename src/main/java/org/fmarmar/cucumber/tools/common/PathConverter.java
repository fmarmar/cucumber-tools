package org.fmarmar.cucumber.tools.common;

import java.nio.file.Path;

public class PathConverter extends com.beust.jcommander.converters.PathConverter {
	
	@Override
	public Path convert(String value) {
	    return super.convert(value).toAbsolutePath();
	  }
	
	
}
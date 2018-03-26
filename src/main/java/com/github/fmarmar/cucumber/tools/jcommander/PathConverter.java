package com.github.fmarmar.cucumber.tools.jcommander;

import java.nio.file.Path;

public class PathConverter extends com.beust.jcommander.converters.PathConverter {
	
	@Override
	public Path convert(String value) {
	    return super.convert(value).toAbsolutePath();
	  }
	
	
}
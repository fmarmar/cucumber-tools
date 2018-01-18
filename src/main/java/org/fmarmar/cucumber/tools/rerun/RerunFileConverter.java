package org.fmarmar.cucumber.tools.rerun;

import java.nio.file.Path;

import org.fmarmar.cucumber.tools.common.PathConverter;

public class RerunFileConverter extends PathConverter {
	
	public static final String DEFAULT_FILE_NAME = "rerun.txt";
	
	@Override
	public Path convert(String value) {
		
		if (value.endsWith("/")) {
			return super.convert(value).resolve(DEFAULT_FILE_NAME);
		}
		
		return super.convert(value);
	}
	
}
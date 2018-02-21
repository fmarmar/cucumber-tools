package com.github.fmarmar.cucumber.tools.report.parser.json.util;

import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.util.RequestPayload;

public class PathRequestPayload extends RequestPayload {
	
	private static final long serialVersionUID = -1158845573715621701L;
	
	private Path path;
	
	public PathRequestPayload(Path path) {
		super(StringUtils.EMPTY);
		this.path = path;
	}
	
	@Override
    public String toString() {
		return path.toString();
	}
	
}
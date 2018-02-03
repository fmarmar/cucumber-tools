package org.fmarmar.cucumber.tools.report.html.support;

import org.apache.commons.lang.StringUtils;

import lombok.Data;

@Data
public class ReportMetadata {
	
	private String projectName = StringUtils.EMPTY;
	
	private String buildId;
	
	private final long buildTimestamp = System.currentTimeMillis();
	
	
	
}
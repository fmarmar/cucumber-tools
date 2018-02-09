package org.fmarmar.cucumber.tools.report.html.support;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

@Data
public class ReportMetadata {
	
	private final String projectName;
	
	private final String buildId;
	
	private final String buildTimestamp;
	
	public ReportMetadata() {
		this(null, null);
	}
	
	public ReportMetadata(String projectName, String buildId) {
		this.projectName = projectName;
		this.buildId = buildId;
		this.buildTimestamp = formatTimestamp(new Date());
	}

	private static String formatTimestamp(Date date) {
		return new SimpleDateFormat("dd MMM yyyy, HH:mm").format(date);
	}
	
	
}
package org.fmarmar.cucumber.tools.report.html.support;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import lombok.Data;

@Data
public class ReportMetadata {
	
	private String projectName = StringUtils.EMPTY;
	
	private String buildId;
	
	private final String buildTimestamp = formatTimestamp(new Date());
	
	private static String formatTimestamp(Date date) {
		return new SimpleDateFormat("dd MMM yyyy, HH:mm").format(date);
	}
	
}
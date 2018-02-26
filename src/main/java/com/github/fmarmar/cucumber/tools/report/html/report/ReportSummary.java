package com.github.fmarmar.cucumber.tools.report.html.report;

import com.github.fmarmar.cucumber.tools.report.model.Feature;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericResult;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericSummary;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ReportSummary {
	
	private final GenericSummary features = new GenericSummary();
	
	private final GenericSummary scenarios = new GenericSummary();

	private long duration = 0;
	
	public void add(Feature feature) {
		GenericResult result = feature.getResult();
		
		duration += result.getDuration();
		features.add(result.getStatus());
	}
	
	public void add(GenericSummary summary) {
		scenarios.addSummary(summary);
	}
	
}
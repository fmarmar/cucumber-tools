package com.github.fmarmar.cucumber.tools.report.html.report;

import com.github.fmarmar.cucumber.tools.report.model.support.GenericStatus;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericSummary;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public abstract class ReportSummary {
	
	private final GenericSummary scenarios = new GenericSummary();
	
	private final GenericSummary total = new GenericSummary();
	
	private long duration = 0;
	
	protected void addDuration(long duration) {
		duration += duration;
	}
	
	protected void addStatus(GenericStatus status) {
		total.add(status);
	}
	
	protected void addScenariosSummary(GenericSummary summary) {
		scenarios.addSummary(summary);
	}
	
}
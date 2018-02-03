package org.fmarmar.cucumber.tools.report.model.support;

import org.fmarmar.cucumber.tools.report.model.Feature;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ReportSummary {
	
	private final GenericSummary features = new GenericSummary();
	
	private final GenericSummary scenarios = new GenericSummary();

	private final StepsSummary steps = new StepsSummary();
	
	public void add(Feature feature) {
		features.add(feature.getResult().getStatus());
	}
	
	public void add(GenericSummary summary) {
		scenarios.addSummary(summary);
	}
	
	public void add(StepsSummary summary) {
		steps.addSummary(summary);
	}
	
	
	
}
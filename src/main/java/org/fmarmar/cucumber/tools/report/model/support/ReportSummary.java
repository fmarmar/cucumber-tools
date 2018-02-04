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
	
	private long duration = 0;
	
	public void add(Feature feature) {
		GenericResult result = feature.getResult();
		
		duration += result.getDuration();
		features.add(result.getStatus());
	}
	
	public void add(GenericSummary summary) {
		scenarios.addSummary(summary);
	}
	
	public void add(StepsSummary summary) {
		steps.addSummary(summary);
	}
	
	
	
}
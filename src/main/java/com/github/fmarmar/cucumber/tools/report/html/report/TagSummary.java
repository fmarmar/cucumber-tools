package com.github.fmarmar.cucumber.tools.report.html.report;

import com.github.fmarmar.cucumber.tools.report.model.Scenario;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericResult;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericStatus;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericSummary;

import lombok.Data;

@Data
public class TagSummary {
	
	private final String name;
	
	private GenericResult result = new GenericResult();
	
	private GenericSummary scenariosSummary = new GenericSummary();
	
	public TagSummary(String tagName) {
		this.name = tagName;
	}
	
	public void collectScenarioInfo(Scenario scenario) {
	
		result.addDuration(scenario.getDuration());
		result.evaluateStatus(scenario.getStatus());
		
		scenariosSummary.add(scenario.getStatus());
	
	}

	// shortcut methods
	
	
	public long getDuration() {
		return result.getDuration();
	}
	
	public GenericStatus getStatus() {
		return result.getStatus();
	}
	
}
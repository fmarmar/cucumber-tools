package com.github.fmarmar.cucumber.tools.report.html.report;

import com.github.fmarmar.cucumber.tools.report.model.Scenario;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericResult;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericSummary;
import com.github.fmarmar.cucumber.tools.report.model.support.StepsSummary;
import com.github.fmarmar.cucumber.tools.report.utils.ReportUtils;

import lombok.Data;

@Data
public class TagSummary {
	
	private final String tag;
	
	private final String uuid;
	
	private GenericResult result = new GenericResult();
	
	private final StepsSummary stepsSummary = new StepsSummary();
	
	private GenericSummary scenariosSummary = new GenericSummary();
	
	public TagSummary(String tag) {
		this.tag = tag;
		this.uuid = ReportUtils.hash(tag);
	}
	
	public void collectScenarioInfo(Scenario scenario) {
	
		result.addDuration(scenario.getDuration());
		result.evaluateStatus(scenario.getStatus());
		
		scenariosSummary.add(scenario.getStatus());
		stepsSummary.addSummary(scenario.getStepsSummary());
	
	}
	
}
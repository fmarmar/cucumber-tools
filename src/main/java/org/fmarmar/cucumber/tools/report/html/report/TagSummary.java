package org.fmarmar.cucumber.tools.report.html.report;

import org.fmarmar.cucumber.tools.report.model.Scenario;
import org.fmarmar.cucumber.tools.report.model.support.GenericResult;
import org.fmarmar.cucumber.tools.report.model.support.GenericSummary;
import org.fmarmar.cucumber.tools.report.model.support.StepsSummary;
import org.fmarmar.cucumber.tools.report.utils.ReportUtils;

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
package com.github.fmarmar.cucumber.tools.report.html.report;

import com.github.fmarmar.cucumber.tools.report.model.Feature;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericResult;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper=true)
public class FeaturesReport extends ReportSummary {
	
	public void add(Feature feature) {
		
		GenericResult result = feature.getResult();
		
		addDuration(result.getDuration());
		addStatus(result.getStatus());
		addScenariosSummary(feature.getScenariosSummary());
		
	}
	
}
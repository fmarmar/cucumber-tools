package com.github.fmarmar.cucumber.tools.report.html.report;

import java.util.ArrayList;
import java.util.Collection;

import com.github.fmarmar.cucumber.tools.report.model.Feature;
import com.github.fmarmar.cucumber.tools.report.model.Scenario;

import lombok.Data;

@Data
public class Failure {
	
	private final Feature feature;
	
	private final Collection<Scenario> scenarios = new ArrayList<>();
	
	public Failure(Feature feature, Scenario failedScenario) {
		this.feature = feature;
		add(failedScenario);
	}

	public void add(Scenario scenario) {
		scenarios.add(scenario);
	}

}

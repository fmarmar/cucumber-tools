package com.github.fmarmar.cucumber.tools.report.model;

import com.github.fmarmar.cucumber.tools.report.model.ExecutionElement;
import com.github.fmarmar.cucumber.tools.report.model.Scenario;
import com.github.fmarmar.cucumber.tools.report.model.ScenarioWithBackground;
import com.google.common.collect.Iterables;

public class ModelTestUtils {
	
	private ModelTestUtils() {}
	
	public static Iterable<ExecutionElement> steps(Scenario scenario) {
		
		if (scenario instanceof ScenarioWithBackground) {
			return steps((ScenarioWithBackground) scenario);
		}
		
		return Iterables.concat(scenario.getBefore(), scenario.getSteps(), scenario.getAfter());
		
	}
	
	public static Iterable<ExecutionElement> steps(ScenarioWithBackground scenario) {
		return Iterables.concat(scenario.getBefore(), scenario.getBackgroundSteps(), scenario.getSteps(), scenario.getAfter());
	}
	
	
	
}
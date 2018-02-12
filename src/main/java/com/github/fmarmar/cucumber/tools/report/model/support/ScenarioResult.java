package com.github.fmarmar.cucumber.tools.report.model.support;

import com.github.fmarmar.cucumber.tools.report.model.ExecutionElement;
import com.github.fmarmar.cucumber.tools.report.model.Result;

import lombok.Data;

@Data
public class ScenarioResult {

	private GenericStatus status = GenericStatus.PASSED;

	// in nanoseconds
	private long duration;

	public void addDuration(long duration) {
		this.duration += duration;
	}

	public void evaluateStatus(StepStatus status) {

		GenericStatus newStatus = GenericStatus.map(status);

		if (newStatus.ordinal() > this.status.ordinal()) {
			this.status = newStatus;
		}

	}
	
	public static ScenarioResult result(Iterable<ExecutionElement> scenarioElements) {
		
		ScenarioResult result = new ScenarioResult();
		
		for (ExecutionElement element: scenarioElements) {
			Result elementResult = element.getResult();
			
			result.addDuration(elementResult.getDuration());
			result.evaluateStatus(elementResult.getStatus());
		}
		
		return result;
		
	}

}
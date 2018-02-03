package org.fmarmar.cucumber.tools.report.model;

import org.fmarmar.cucumber.tools.report.model.support.StepStatus;

import lombok.Data;

@Data
public class Result {
	
	private StepStatus status = StepStatus.UNDEFINED;
	
	// in nanoseconds
	private long duration;
	
	private String errorMessage;
	
}
package com.github.fmarmar.cucumber.tools.report.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.fmarmar.cucumber.tools.report.model.support.StepStatus;

import lombok.Data;

@Data
public class Result {
	
	private StepStatus status = StepStatus.UNDEFINED;
	
	// in nanoseconds
	private long duration;
	
	@JsonProperty("error_message")
	private String errorMessage;
	
}
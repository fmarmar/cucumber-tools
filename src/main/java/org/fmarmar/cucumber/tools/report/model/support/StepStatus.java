package org.fmarmar.cucumber.tools.report.model.support;

import org.fmarmar.cucumber.tools.report.json.deser.StepStatusDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using=StepStatusDeserializer.class)
public enum StepStatus {

    PASSED,
    SKIPPED,
    FAILED,
    PENDING,
    UNDEFINED;
	
	
}
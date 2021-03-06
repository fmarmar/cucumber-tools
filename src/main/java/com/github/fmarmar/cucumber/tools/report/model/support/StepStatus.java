package com.github.fmarmar.cucumber.tools.report.model.support;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.fmarmar.cucumber.tools.report.parser.json.deser.StepStatusDeserializer;

@JsonDeserialize(using=StepStatusDeserializer.class)
public enum StepStatus {

    PASSED,
    UNDEFINED,
    PENDING,
    SKIPPED,
	FAILED;
	
	public boolean isPassed() {
		return this == PASSED;
	}
	
	public String getLabel() {
		return StringUtils.capitalize(toString());
	}
	
	@Override
	public String toString() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}
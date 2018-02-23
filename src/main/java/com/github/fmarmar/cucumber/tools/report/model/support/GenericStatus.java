package com.github.fmarmar.cucumber.tools.report.model.support;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public enum GenericStatus {

    PASSED,
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
	
	public static GenericStatus map(StepStatus status) {

		switch (status) {
			case PASSED:
				return PASSED;
			case UNDEFINED:
			case PENDING:
			case SKIPPED:
				return SKIPPED;
			case FAILED:
				return FAILED;
			default: // Should never happen
				throw new IllegalArgumentException("Unknown StepStatus " + status);
		}

	}
	
}
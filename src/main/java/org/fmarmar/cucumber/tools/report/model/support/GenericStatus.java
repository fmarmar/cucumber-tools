package org.fmarmar.cucumber.tools.report.model.support;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

public enum GenericStatus {

    PASSED,
    SKIPPED,
    FAILED;
	
	public boolean isPassed() {
		return this == PASSED;
	}
	
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}
	
	public String getLabel() {
		return StringUtils.capitalize(getName());
	}
	
}
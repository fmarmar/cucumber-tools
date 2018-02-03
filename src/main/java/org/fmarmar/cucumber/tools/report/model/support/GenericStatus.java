package org.fmarmar.cucumber.tools.report.model.support;

import org.apache.commons.lang.StringUtils;

public enum GenericStatus {

    PASSED,
    SKIPPED,
    FAILED;
	
	public String getLabel() {
		return StringUtils.capitalize(name());
	}
	
}
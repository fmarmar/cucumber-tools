package com.github.fmarmar.cucumber.tools.report.model.support;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

public enum ScenarioType {

    BACKGROUND,
    SCENARIO,
    SCENARIO_OUTLINE;
	
	public String toString() {
		return StringUtils.capitalize(this.name().toLowerCase(Locale.ENGLISH));
	}
	
}
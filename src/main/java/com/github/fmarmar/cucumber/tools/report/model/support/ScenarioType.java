package com.github.fmarmar.cucumber.tools.report.model.support;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public enum ScenarioType {

    BACKGROUND,
    SCENARIO,
    SCENARIO_OUTLINE;
	
	@Override
	public String toString() {
		return StringUtils.capitalize(this.name().toLowerCase(Locale.ENGLISH));
	}
	
}
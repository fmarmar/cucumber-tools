package org.fmarmar.cucumber.tools.report.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.fmarmar.cucumber.tools.report.json.deser.TagDeserializer;
import org.fmarmar.cucumber.tools.report.model.support.GenericStatus;
import org.fmarmar.cucumber.tools.report.model.support.NamedElement;
import org.fmarmar.cucumber.tools.report.model.support.PostProcessor;
import org.fmarmar.cucumber.tools.report.model.support.ScenarioResult;
import org.fmarmar.cucumber.tools.report.model.support.ScenarioType;
import org.fmarmar.cucumber.tools.report.model.support.StepsSummary;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Iterables;

import lombok.Data;

@Data
public class Scenario implements NamedElement, PostProcessor {
	
	protected String id;
	
	protected String name;
	
	protected String description = StringUtils.EMPTY;
	
	protected ScenarioType type = ScenarioType.SCENARIO;

	@JsonDeserialize(contentUsing=TagDeserializer.class)
	protected Set<String> tags = new HashSet<>();
	
	protected List<ScenarioHook> before = Collections.emptyList();
	
	protected List<Step> steps = Collections.emptyList();
	
	protected List<ScenarioHook> after = Collections.emptyList();
	
	protected StepsSummary stepsSummary;
	
	protected ScenarioResult result;

	@Override
	public void postProcess() {
		stepsSummary = summary(steps);
		result = ScenarioResult.result(Iterables.concat(before, steps, after));
	}
	
	protected StepsSummary summary(Iterable<Step> steps) {
		
		StepsSummary summary = new StepsSummary();
		
		for (ExecutionElement step : steps) {
			summary.addStep(step.getResult().getStatus());
		}
		
		return summary;
	}
	
	// Shortcut methods
	
	public long getDuration() {
		return result.getDuration();
	}
	
	public GenericStatus getStatus() {
		return result.getStatus();
	}
	
}
package com.github.fmarmar.cucumber.tools.report.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericStatus;
import com.github.fmarmar.cucumber.tools.report.model.support.NamedElement;
import com.github.fmarmar.cucumber.tools.report.model.support.PostProcessor;
import com.github.fmarmar.cucumber.tools.report.model.support.ScenarioResult;
import com.github.fmarmar.cucumber.tools.report.model.support.ScenarioType;
import com.github.fmarmar.cucumber.tools.report.model.support.StepsSummary;
import com.github.fmarmar.cucumber.tools.report.parser.json.deser.TagDeserializer;
import com.google.common.collect.Iterables;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Scenario implements NamedElement, PostProcessor {
	
	protected String id;
	
	protected String name;
	
	protected String description = StringUtils.EMPTY;
	
	protected ScenarioType type = ScenarioType.SCENARIO;

	@JsonDeserialize(contentUsing=TagDeserializer.class)
	protected Set<String> tags = new HashSet<>();
	
	protected List<ScenarioHook> before = new ArrayList<>();
	
	protected List<Step> steps = Collections.emptyList();
	
	protected List<ScenarioHook> after = new ArrayList<>();
	
	protected StepsSummary stepsSummary;
	
	protected ScenarioResult result;

	@Override
	public void postProcess() {
		processSteps();
		stepsSummary = summary(steps);
		result = ScenarioResult.result(getExecutionElements());
	}
	
	private void processSteps() { 
		
		Iterator<Step> it = steps.iterator();
		
		while (it.hasNext()) {
			Step currentStep = it.next();
			
			if (currentStep.isHidden() && addToScenarioHook(currentStep)) {
				it.remove();
			}
			
		}
		
	}

	private boolean addToScenarioHook(Step step) {
		
		ScenarioHook hook = new ScenarioHook(step.getResult(), step.getLocation(), step.getOutputs(), step.getEmbeddings());

		String keyword = step.getKeyword().trim().toUpperCase(Locale.ENGLISH);
		
		switch (keyword) {
			case "BEFORE":
				return before.add(hook);
			case "AFTER":
				return after.add(hook);
			default:
				log.warn("Unknown hidden step {}. Keeping as step", keyword);
				return false;
		}
		
	}

	protected StepsSummary summary(Iterable<Step> steps) {
		
		StepsSummary summary = new StepsSummary();
		
		for (ExecutionElement step : steps) {
			summary.addStep(step.getResult().getStatus());
		}
		
		return summary;
	}
	
	public Iterable<ExecutionElement> getExecutionElements() {
		return Iterables.concat(before, executionElements(steps), after);
	}
	
	protected static final Iterable<ExecutionElement> executionElements(Collection<Step> steps) {
		
		List<ExecutionElement> executionElements = new ArrayList<>(steps.size());
		
		for (Step step : steps) {
			Iterables.addAll(executionElements, step.getExecutionElements());
		}
		
		return executionElements;
		
	}
	
	// Shortcut methods
	
	public long getDuration() {
		return result.getDuration();
	}
	
	public GenericStatus getStatus() {
		return result.getStatus();
	}
	
}
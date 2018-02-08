package org.fmarmar.cucumber.tools.report.model;

import java.util.Collections;
import java.util.List;

import org.fmarmar.cucumber.tools.report.model.support.ScenarioResult;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ScenarioWithBackground extends Scenario {

	private String backgroundName;
	
	private List<Step> backgroundSteps = Collections.emptyList();
	
	public ScenarioWithBackground(Scenario scenario, Scenario background) {
		
		this.id = scenario.id;
		this.name = scenario.name;
		this.description = scenario.description;
		this.type = scenario.type;
		this.tags = Sets.newHashSet(Iterables.concat(background.tags, scenario.tags));
		this.before = Lists.newArrayList(Iterables.concat(background.before, scenario.before));
		this.backgroundName = background.name;
		this.backgroundSteps = background.steps;
		this.steps = scenario.steps;
		this.after = scenario.after;
		
	}
	
	@Override
	public void postProcess() {
		stepsSummary = summary(Iterables.concat(backgroundSteps, steps));
		result = ScenarioResult.result(getExecutionElements());
	}
	
	@Override
	public Iterable<ExecutionElement> getExecutionElements() {
		return Iterables.concat(before, executionElements(backgroundSteps), executionElements(steps), after);
	}
	
	
}
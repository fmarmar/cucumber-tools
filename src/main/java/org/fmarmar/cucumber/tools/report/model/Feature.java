package org.fmarmar.cucumber.tools.report.model;

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.fmarmar.cucumber.tools.report.json.deser.TagDeserializer;
import org.fmarmar.cucumber.tools.report.json.util.FeaturePostProcessor;
import org.fmarmar.cucumber.tools.report.model.support.GenericResult;
import org.fmarmar.cucumber.tools.report.model.support.GenericStatus;
import org.fmarmar.cucumber.tools.report.model.support.GenericSummary;
import org.fmarmar.cucumber.tools.report.model.support.PostProcessor;
import org.fmarmar.cucumber.tools.report.model.support.ScenarioType;
import org.fmarmar.cucumber.tools.report.model.support.StepsSummary;
import org.fmarmar.cucumber.tools.report.utils.ReportUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@Data
@JsonDeserialize(converter=FeaturePostProcessor.class)
public class Feature implements PostProcessor {

	private String uri;

	private String id;

	private String name;

	private String description;

	@JsonDeserialize(contentUsing = TagDeserializer.class)
	private Set<String> tags = new HashSet<>();

	@JsonProperty("elements")
	private List<Scenario> scenarios;

	private String uuid;

	private GenericSummary scenariosSummary;

	private StepsSummary stepsSummary;

	private GenericResult result; 

	@Override
	public void postProcess() {
		uuid = ReportUtils.hash(uri);
		processScenarios();
	}

	private void processScenarios() {

		scenariosSummary = new GenericSummary();
		stepsSummary = new StepsSummary();
		result = new GenericResult();

		ListIterator<Scenario> it = scenarios.listIterator();
		Scenario background = null;

		while(it.hasNext()) {
			Scenario currentScenario = it.next();

			if (currentScenario.getType() == ScenarioType.BACKGROUND) {
				background = currentScenario;
				it.remove();
			} else {

				// Merge Background if necessary
				currentScenario = addBackground(currentScenario, background);
				it.set(currentScenario);
				background = null;

				// Add Feature tags
				currentScenario.getTags().addAll(tags);

				// postProcess scenario
				currentScenario.postProcess();

				// collect Scenario data
				result.addDuration(currentScenario.getDuration());
				result.evaluateStatus(currentScenario.getStatus());

				scenariosSummary.add(currentScenario.getStatus());
				stepsSummary.addSummary(currentScenario.stepsSummary);

			}

		}

	}

	private Scenario addBackground(Scenario scenario, Scenario background) {

		if (background == null) {
			return scenario;
		}

		return new ScenarioWithBackground(scenario, background);

	}

	// Shortcut methods

	public long getDuration() {
		return result.getDuration();
	}

	public GenericStatus getStatus() {
		return result.getStatus();
	}

}
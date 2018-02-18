package com.github.fmarmar.cucumber.tools.report.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericResult;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericStatus;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericSummary;
import com.github.fmarmar.cucumber.tools.report.model.support.NamedElement;
import com.github.fmarmar.cucumber.tools.report.model.support.PostProcessor;
import com.github.fmarmar.cucumber.tools.report.model.support.ScenarioType;
import com.github.fmarmar.cucumber.tools.report.model.support.StepsSummary;
import com.github.fmarmar.cucumber.tools.report.parser.json.deser.TagDeserializer;
import com.github.fmarmar.cucumber.tools.report.parser.json.util.FeaturePostProcessor;
import com.github.fmarmar.cucumber.tools.report.utils.ReportUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonDeserialize(converter=FeaturePostProcessor.class)
public class Feature implements NamedElement, PostProcessor {

	private String id;

	private String uri;

	private String name = StringUtils.EMPTY;

	private String description = StringUtils.EMPTY;

	@JsonDeserialize(contentUsing = TagDeserializer.class)
	private Set<String> tags = new HashSet<>();

	@JsonProperty("elements")
	private List<Scenario> scenarios = Collections.emptyList();

	private String uuid;

	private GenericSummary scenariosSummary;

	private StepsSummary stepsSummary;

	private GenericResult result;
	
	private Metadata metadata = Metadata.NO_METADATA_INSTANCE;
	
	public Feature(String id, String uri) {
		this.id = id;
		this.uri = uri;
	}

	@Override
	public void postProcess() {
		uuid = ReportUtils.hash(uri + metadata);
		processScenarios();
	}

	private void processScenarios() {

		scenariosSummary = new GenericSummary();
		stepsSummary = new StepsSummary();
		result = new GenericResult();

		ListIterator<Scenario> it = scenarios.listIterator();
		Scenario background = null;

		while (it.hasNext()) {
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
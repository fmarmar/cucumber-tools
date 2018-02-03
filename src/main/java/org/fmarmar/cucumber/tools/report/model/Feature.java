package org.fmarmar.cucumber.tools.report.model;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.fmarmar.cucumber.tools.report.json.deser.TagDeserializer;
import org.fmarmar.cucumber.tools.report.json.util.FeaturePostProcessor;
import org.fmarmar.cucumber.tools.report.model.support.FeatureResult;
import org.fmarmar.cucumber.tools.report.model.support.GenericSummary;
import org.fmarmar.cucumber.tools.report.model.support.PostProcessor;
import org.fmarmar.cucumber.tools.report.model.support.ScenarioType;
import org.fmarmar.cucumber.tools.report.model.support.StepsSummary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import lombok.Data;

@Data
@JsonDeserialize(converter=FeaturePostProcessor.class)
public class Feature implements PostProcessor {
	
	private static final HashFunction HF = Hashing.murmur3_128();
	
	private String uri;
	
	private String id;
	
	private String name;
	
	private String description;
	
	@JsonDeserialize(contentUsing=TagDeserializer.class)
	private Set<String> tags = new HashSet<>();
	
	@JsonProperty("elements")
	private List<Scenario> scenarios;
	
	private String uuid;

	private GenericSummary scenariosSummary;
	
	private StepsSummary stepsSummary;
	
	private FeatureResult result; 
	
	@Override
	public void postProcess() {
		uuid = hash();
		processScenarios();
	}
	
	private String hash() {
		return HF.newHasher().putString(uri, StandardCharsets.UTF_8).hash().toString();
	}
	
	
	private void processScenarios() {
		
		scenariosSummary = new GenericSummary();
		stepsSummary = new StepsSummary();
		result = new FeatureResult();
		
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
				
				// collect Feature data
				scenariosSummary.add(currentScenario.result.getStatus());
				stepsSummary.addSummary(currentScenario.stepsSummary);
				
				result.addDuration(currentScenario.result.getDuration());
				result.evaluateStatus(currentScenario.result.getStatus());
				
			}
			
		}
		
	}
	
	private Scenario addBackground(Scenario scenario, Scenario background) {
	
		if (background == null) {
			return scenario;
		}
		
		return new ScenarioWithBackground(scenario, background);
		
	}
	
}
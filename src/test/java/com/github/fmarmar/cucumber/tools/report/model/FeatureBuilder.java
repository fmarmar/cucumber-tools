package com.github.fmarmar.cucumber.tools.report.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.fmarmar.cucumber.tools.report.model.support.GenericStatus;
import com.github.fmarmar.cucumber.tools.report.model.support.ScenarioResult;
import com.github.fmarmar.cucumber.tools.report.model.support.ScenarioType;
import com.github.fmarmar.cucumber.tools.report.model.support.StepStatus;
import com.google.common.collect.Iterables;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.FieldDefinitionBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

public class FeatureBuilder {
	
	private static EnhancedRandom FEATURE_BUILDER = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
			.exclude(FieldDefinitionBuilder.field().named("uuid").get())
			.exclude(FieldDefinitionBuilder.field().named("scenariosSummary").get())
			.exclude(FieldDefinitionBuilder.field().named("stepsSummary").get())
			.exclude(FieldDefinitionBuilder.field().named("result").get())
			.build();
	
	private static EnhancedRandom SCENARIO_BUILDER = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
			.overrideDefaultInitialization(true)
			.collectionSizeRange(1, 3)
			.exclude(FieldDefinitionBuilder.field().named("type").ofType(ScenarioType.class).get())
			.exclude(FieldDefinitionBuilder.field().named("rows").get())
			.exclude(FieldDefinitionBuilder.field().named("before").inClass(Step.class).get())
			.exclude(FieldDefinitionBuilder.field().named("after").inClass(Step.class).get())
			.exclude(FieldDefinitionBuilder.field().named("outputs").get())
			.exclude(FieldDefinitionBuilder.field().named("embeddings").get())
			.exclude(FieldDefinitionBuilder.field().named("stepsSummary").get())
			.exclude(FieldDefinitionBuilder.field().named("result").ofType(ScenarioResult.class).get())
			.build();
			
	private Random rand = new Random();
	
	private Feature feature;
	
	private FeatureBuilder() {
		this.feature = FEATURE_BUILDER.nextObject(Feature.class);
	};
	
	public Feature build() {
		feature.postProcess();
		return feature;
	}
	
	public static FeatureBuilder newFeature() {
		return new FeatureBuilder();
	}
	
	public FeatureBuilder withRandoScenarios(int numScenarios) {
		
		List<Scenario> scenarios = new ArrayList<>(numScenarios);
		feature.setScenarios(scenarios);
		
		for (int counter = 0; counter < numScenarios; counter ++) {
			scenarios.add(randomScenario());
		}
		
		return this;
		
	}
	
	private Scenario randomScenario() {
		return SCENARIO_BUILDER.nextObject(Scenario.class);
	}
	
	public FeatureBuilder withScenarios(GenericStatus... results) {
		
		List<Scenario> scenarios = new ArrayList<>(results.length);
		feature.setScenarios(scenarios);
		
		for (GenericStatus result : results) {
			scenarios.add(randomScenario(result));
		}	
		
		return this;
	}
	
	private Scenario randomScenario(GenericStatus result) {
		return ensureScenarioResult(randomScenario(), result);
	}
	
	private Scenario ensureScenarioResult(Scenario scenario, GenericStatus result) {
		
		Iterable<ExecutionElement> scenarioSteps = ModelTestUtils.steps(scenario);
		
		boolean changeSteps = true;
		
		for (ExecutionElement step : scenarioSteps) {
			
			StepStatus stepStatus = step.getStatus();
			
			if (result == GenericStatus.map(stepStatus)) {
				changeSteps = false;
			} else {
				step.getResult().setStatus(StepStatus.PASSED);
			}
		}
		
		if (changeSteps) {
			ExecutionElement step = Iterables.get(scenarioSteps, rand.nextInt(Iterables.size(scenarioSteps)));
			step.getResult().setStatus(map(result));
		}
		
		return scenario;
		
	}
	
	private StepStatus map(GenericStatus status) {
		switch(status) {
			case PASSED:
				return StepStatus.PASSED;
			case SKIPPED:
				return StepStatus.PENDING;
			case FAILED:
				return StepStatus.FAILED;
			default:
				throw new IllegalArgumentException("Unknown GenericStatus: " + status);
		}
	}
	
}
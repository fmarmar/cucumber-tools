package org.fmarmar.cucumber.tools.report.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.fmarmar.cucumber.tools.report.model.support.GenericStatus;
import org.fmarmar.cucumber.tools.report.model.support.ScenarioResult;
import org.fmarmar.cucumber.tools.report.model.support.StepStatus;

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
			.exclude(FieldDefinitionBuilder.field().named("stepsSummary").get())
			.exclude(FieldDefinitionBuilder.field().named("result").ofType(ScenarioResult.class).get())
			.exclude(FieldDefinitionBuilder.field().named("embeddings").get())
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
	
	public FeatureBuilder withScenarios(GenericStatus... results) {
		
		List<Scenario> scenarios = new ArrayList<>(results.length);
		feature.setScenarios(scenarios);
		
		for (GenericStatus result : results) {
			scenarios.add(randomScenario(result));
		}	
		
		return this;
	}
	
	private Scenario randomScenario(GenericStatus result) {
		
		Scenario scenario = SCENARIO_BUILDER.nextObject(Scenario.class);
		ensureScenarioResult(scenario, result);
		
		return scenario;
	}
	
	private void ensureScenarioResult(Scenario scenario, GenericStatus result) {
		
		Iterable<ExecutionElement> allSteps = Iterables.concat(scenario.getBefore(), scenario.getSteps(), scenario.getAfter());
		
		if (scenario instanceof ScenarioWithBackground) {
			allSteps = Iterables.concat(allSteps, ((ScenarioWithBackground) scenario).getBackgroundSteps());
		}
		
		boolean changeSteps = true;
		
		for (ExecutionElement step : allSteps) {
			
			StepStatus stepStatus = step.getStatus();
			
			if (result != GenericStatus.map(stepStatus)) {
				step.getResult().setStatus(StepStatus.PASSED);
				changeSteps = false;
			}
		}
		
		if (changeSteps) {
			ExecutionElement step = Iterables.get(allSteps, rand.nextInt(Iterables.size(allSteps)));
			step.getResult().setStatus(map(result));
		}
		
	}
	
	private StepStatus map(GenericStatus status) {
		switch(status) {
			case PASSED:
				return StepStatus.PASSED;
			case SKIPPED:
				return StepStatus.SKIPPED;
			case FAILED:
				return StepStatus.FAILED;
			default:
				throw new IllegalArgumentException("Unknown GenericStatus: " + status);
		}
	}
	
}
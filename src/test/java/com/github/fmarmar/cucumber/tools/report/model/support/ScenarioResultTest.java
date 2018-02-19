package com.github.fmarmar.cucumber.tools.report.model.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class ScenarioResultTest {
	
	private ScenarioResult result;
	
	@Before
	public void configureTest() {
		result = new ScenarioResult();
	}
	
	@SuppressWarnings("unused")
	private Object evaluateStatusTests() {
		return new Object[][] {
				{"PASSED", "PASSED"},
				{"SKIPPED", "SKIPPED"},
				{"SKIPPED", "UNDEFINED"},
				{"SKIPPED", "PENDING"},
				{"FAILED", "FAILED"},
				{"PASSED", "PASSED", "PASSED", "PASSED"},
				{"FAILED", "PASSED", "FAILED", "SKIPPED"},
				{"FAILED", "PASSED", "PASSED", "FAILED"},
				{"SKIPPED", "SKIPPED", "SKIPPED", "SKIPPED"},
				{"SKIPPED", "PENDING", "SKIPPED", "SKIPPED"},
				{"SKIPPED", "PASSED", "PASSED", "UNDEFINED", "SKIPPED"}
				
				
		};
	}
	
	@Test
	@Parameters(method = "evaluateStatusTests")
	public void testEvaluateStatus(String expectedStatus, String... stepsStatus) {
		
		for (String status : stepsStatus) {
			result.evaluateStatus(StepStatus.valueOf(status));
		}
		
		assertThat(result.getStatus()).isEqualTo(GenericStatus.valueOf(expectedStatus));
		
	}
	
	@Test
	public void evaluateResultsShouldConsiderAllStatuses() {
		
		for (StepStatus status: StepStatus.values()) {
			result.evaluateStatus(status); // This method throw an exception if the enum value is unknown
		}
		
	}
	
	
	
	
}
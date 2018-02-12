package com.github.fmarmar.cucumber.tools.report.model.support;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.fmarmar.cucumber.tools.report.model.support.GenericStatus;
import com.github.fmarmar.cucumber.tools.report.model.support.ScenarioResult;
import com.github.fmarmar.cucumber.tools.report.model.support.StepStatus;

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
				
		assertThat(result.getStatus(), equalTo(GenericStatus.valueOf(expectedStatus)));
		
	}
	
	@Test
	public void evaluateResultsShouldConsiderAllStatuses() {
		
		for (StepStatus status: StepStatus.values()) {
			result.evaluateStatus(status);
		}
		
	}
	
	
	
	
}
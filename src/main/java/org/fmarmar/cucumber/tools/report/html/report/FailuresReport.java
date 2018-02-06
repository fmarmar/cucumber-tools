package org.fmarmar.cucumber.tools.report.html.report;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.fmarmar.cucumber.tools.report.model.Feature;
import org.fmarmar.cucumber.tools.report.model.Scenario;

import lombok.Data;

@Data
public class FailuresReport {

	private final Map<String, Failure> failures = new HashMap<>();
	
	public Collection<Failure> getFailures() {
		return failures.values();
	}

	public void addFailure(Feature feature, Scenario scenario) {
		
		String mapKey = feature.getUuid();
		
		if (failures.containsKey(mapKey)) {
			failures.get(mapKey).add(scenario);
		} else {
			failures.put(mapKey, new Failure(feature, scenario));
		}
		
	}
	
}

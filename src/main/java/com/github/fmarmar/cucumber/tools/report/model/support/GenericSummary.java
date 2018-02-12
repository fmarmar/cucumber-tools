package com.github.fmarmar.cucumber.tools.report.model.support;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class GenericSummary {
	
	protected int passed = 0;
	
	protected int skipped = 0;
	
	protected int failed = 0;
	
	public void add(GenericStatus status) {
		
		switch (status) {
			case PASSED:
				passed++;
				break;
			case SKIPPED:
				skipped++;
				break;
			case FAILED:
				failed++;
				break;
			default: //Should never happen
				throw new IllegalArgumentException("Unknwon ScenarioStatus " + status);
				
		}
		
	}
	
	public void addSummary(GenericSummary summary) {
		
		passed += summary.passed;
		skipped += summary.skipped;
		failed += summary.failed;
		
	}
	
	public int getTotal() {
		return passed + skipped + failed;
	}
	
}
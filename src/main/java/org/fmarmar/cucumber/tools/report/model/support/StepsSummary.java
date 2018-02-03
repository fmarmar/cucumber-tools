package org.fmarmar.cucumber.tools.report.model.support;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class StepsSummary {
	
	private int passed = 0;
	
	private int skipped = 0;
	
	private int failed = 0;
	
	private int pending = 0;
	
	private int undefined = 0;
	
	public void addStep(StepStatus status) {
		
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
			case PENDING:
				pending++;
				break;
			case UNDEFINED:
				undefined++;
				break;
			default: //Should never happen
				throw new IllegalArgumentException("Unknwon StepStatus " + status);
				
		}
		
	}
	
	public void addSummary(StepsSummary stepsSummary) {
		
		passed += stepsSummary.passed;
		skipped += stepsSummary.skipped;
		failed += stepsSummary.failed;
		pending += stepsSummary.pending;
		undefined += stepsSummary.undefined;
		
	}
	
	public int getTotal() {
		return passed + skipped + failed + pending + undefined;
	}

}
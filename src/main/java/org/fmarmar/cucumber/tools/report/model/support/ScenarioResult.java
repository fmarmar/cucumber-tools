package org.fmarmar.cucumber.tools.report.model.support;

import lombok.Data;

@Data
public class ScenarioResult {

	private GenericStatus status = GenericStatus.PASSED;

	// in nanoseconds
	private long duration;

	public void addDuration(long duration) {
		this.duration += duration;
	}

	public void evaluateStatus(StepStatus status) {

		GenericStatus newStatus = map(status);

		if (newStatus.ordinal() > this.status.ordinal()) {
			this.status = newStatus;
		}

	}

	private GenericStatus map(StepStatus status) {

		switch (status) {
			case PASSED:
				return GenericStatus.PASSED;
			case SKIPPED:
			case UNDEFINED:
			case PENDING:
				return GenericStatus.SKIPPED;
			case FAILED:
				return GenericStatus.FAILED;
			default: // Should never happen
				throw new IllegalArgumentException("Unknown StepStatus " + status);
		}

	}

}
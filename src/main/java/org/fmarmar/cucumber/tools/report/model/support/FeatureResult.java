package org.fmarmar.cucumber.tools.report.model.support;

import lombok.Data;

@Data
public class FeatureResult {

	private GenericStatus status = GenericStatus.PASSED;

	// in nanoseconds
	private long duration;

	public void addDuration(long duration) {
		this.duration += duration;
	}

	public void evaluateStatus(GenericStatus status) {

		if (status.ordinal() > this.status.ordinal()) {
			this.status = status;
		}

	}

}
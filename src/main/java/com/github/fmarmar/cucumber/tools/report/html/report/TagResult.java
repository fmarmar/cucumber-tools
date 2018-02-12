package com.github.fmarmar.cucumber.tools.report.html.report;

import com.github.fmarmar.cucumber.tools.report.model.support.GenericStatus;

import lombok.Data;

@Data
public class TagResult {

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
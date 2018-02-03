package org.fmarmar.cucumber.tools.report.model;

import java.util.Collections;
import java.util.List;

import org.fmarmar.cucumber.tools.report.json.deser.MatchDeserializer;
import org.fmarmar.cucumber.tools.report.model.support.StepStatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@Data
public abstract class ExecutionElement {

	private Result result;

	@JsonProperty("match")
	@JsonDeserialize(using=MatchDeserializer.class)
	private String location;
	
	@JsonProperty("output")
	private List<String> outputs = Collections.emptyList();
	
	private List<Embedding> embeddings = Collections.emptyList();

	// Shortcut methods

	public long getDuration() {
		return result.getDuration();
	}

	public StepStatus getStatus() {
		return result.getStatus();
	}
}

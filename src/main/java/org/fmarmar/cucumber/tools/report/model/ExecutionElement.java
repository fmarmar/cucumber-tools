package org.fmarmar.cucumber.tools.report.model;

import java.util.Collections;
import java.util.List;

import org.fmarmar.cucumber.tools.report.model.support.StepStatus;
import org.fmarmar.cucumber.tools.report.parser.json.deser.MatchDeserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class ExecutionElement {

	protected Result result;

	@JsonProperty("match")
	@JsonDeserialize(using = MatchDeserializer.class)
	protected String location;
	
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

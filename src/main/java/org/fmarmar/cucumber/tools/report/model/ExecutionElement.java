package org.fmarmar.cucumber.tools.report.model;

import org.fmarmar.cucumber.tools.report.json.deser.MatchDeserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@Data
public abstract class ExecutionElement {

	private Result result;
	
	@JsonProperty("match")
	@JsonDeserialize(using=MatchDeserializer.class)
	private String location;
	
}

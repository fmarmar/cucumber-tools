package com.github.fmarmar.cucumber.tools.report.model;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class ScenarioHook extends ExecutionElement {

	public ScenarioHook(Result result, String location, List<String> outputs, List<Embedding> embeddings) {
		super(result, location, outputs, embeddings);
	}
	
}

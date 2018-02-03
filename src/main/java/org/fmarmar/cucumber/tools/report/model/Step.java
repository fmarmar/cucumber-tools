package org.fmarmar.cucumber.tools.report.model;

import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class Step extends ExecutionElement {
	
	private String keyword;
	
	private String name;
	
	private List<Row> rows = Collections.emptyList();

	private List<StepHook> before = Collections.emptyList();
	
	private List<StepHook> after = Collections.emptyList();
	
}

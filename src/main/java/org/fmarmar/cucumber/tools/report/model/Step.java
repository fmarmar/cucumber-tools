package org.fmarmar.cucumber.tools.report.model;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.fmarmar.cucumber.tools.report.model.support.PostProcessor;
import org.fmarmar.cucumber.tools.report.parser.json.util.StepPostProcessor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Iterables;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonDeserialize(converter=StepPostProcessor.class)
public class Step extends ExecutionElement implements PostProcessor {
	
	private static final Pattern JS_LOCATION_PATTERN = Pattern.compile(".*\\.js:\\d+$");
	
	private String keyword;
	
	private String name;
	
	private boolean hidden = false;
	
	private List<Row> rows = Collections.emptyList();

	private List<StepHook> after = Collections.emptyList();

	@Override
	public void postProcess() {
		
		if (isJsStep()) {
			result.setDuration(TimeUnit.MILLISECONDS.toNanos(result.getDuration()));
		}
		
	}

	private boolean isJsStep() {
		return JS_LOCATION_PATTERN.matcher(location).matches();
	}
	
	Iterable<ExecutionElement> getExecutionElements() {
		return Iterables.concat(Collections.singleton(this), after);
	}
	
}

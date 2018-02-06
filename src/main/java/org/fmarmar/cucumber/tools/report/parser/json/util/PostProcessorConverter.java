package org.fmarmar.cucumber.tools.report.parser.json.util;

import org.fmarmar.cucumber.tools.report.model.support.PostProcessor;

import com.fasterxml.jackson.databind.util.StdConverter;

public abstract class PostProcessorConverter<IN extends PostProcessor, OUT extends PostProcessor> extends StdConverter<IN, OUT> {
	
	@SuppressWarnings("unchecked")
	public OUT convert(IN value) {
		value.postProcess();
		return (OUT) value;
	}
	
	
}
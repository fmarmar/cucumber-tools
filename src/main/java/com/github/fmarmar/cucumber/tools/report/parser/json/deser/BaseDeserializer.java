package com.github.fmarmar.cucumber.tools.report.parser.json.deser;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.fmarmar.cucumber.tools.report.parser.ReportParser;
import com.github.fmarmar.cucumber.tools.report.parser.ReportParser.ParserConfiguration;

public abstract class BaseDeserializer<T> extends JsonDeserializer<T> {
	
	protected ParserConfiguration getConfiguration(DeserializationContext context) throws JsonMappingException {
		return (ParserConfiguration) context.findInjectableValue(ReportParser.CONFIG_ID, null, null);
	}
	
}
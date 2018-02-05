package org.fmarmar.cucumber.tools.report.json.deser;

import org.fmarmar.cucumber.tools.report.ReportParser;
import org.fmarmar.cucumber.tools.report.ReportParser.ParserConfiguration;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

public abstract class BaseDeserializer<T> extends JsonDeserializer<T> {
	
	protected ParserConfiguration getConfiguration(DeserializationContext context) throws JsonMappingException {
		return (ParserConfiguration) context.findInjectableValue(ReportParser.CONFIG_ID, null, null);
	}
	
}
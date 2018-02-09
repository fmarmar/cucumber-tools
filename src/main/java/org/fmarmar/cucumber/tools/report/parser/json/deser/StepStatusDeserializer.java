package org.fmarmar.cucumber.tools.report.parser.json.deser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.fmarmar.cucumber.tools.report.model.support.StepStatus;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;


/**
 * Deserializes Status and maps all known but not supported into UNDEFINED status.
 *
 */
public class StepStatusDeserializer extends JsonDeserializer<StepStatus> {
	
	/**
	 * https://github.com/cucumber/cucumber-js/blob/1.x/lib/cucumber/status.js
	 */
	public static final List<String> UNKNOWN_STATUSES = Arrays.asList("ambiguous");
	
	
	@Override
    public boolean isCachable() { 
		return true; 
	}

	@Override
	public StepStatus deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		String fieldValue = p.getValueAsString();
		
		return StepStatus.valueOf(map(fieldValue).toUpperCase(Locale.ENGLISH));
	}
	
	private String map(String originalValue) {
	
		if (UNKNOWN_STATUSES.contains(originalValue)) {
			return StepStatus.UNDEFINED.name();
		}
	
		return originalValue;
	}
	
	
}
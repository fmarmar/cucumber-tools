package com.github.fmarmar.cucumber.tools.report.parser.json.deser;

import java.io.IOException;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.fmarmar.cucumber.tools.report.model.support.ScenarioType;

public class ScenarioTypeDeserializer extends JsonDeserializer<ScenarioType> {
	
	@Override
    public boolean isCachable() { 
		return true; 
	}

	@Override
	public ScenarioType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		String fieldValue = p.getValueAsString();
		
		return ScenarioType.valueOf(fieldValue.replace(' ', '_').toUpperCase(Locale.ENGLISH));
	}
	
}
package org.fmarmar.cucumber.tools.report.json.deser;

import java.io.IOException;
import java.util.Locale;

import org.fmarmar.cucumber.tools.report.model.support.ScenarioType;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class ScenarioTypeDeserializer extends JsonDeserializer<ScenarioType> {
	
	private static final long serialVersionUID = 8749674249395896103L;

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
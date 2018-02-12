package com.github.fmarmar.cucumber.tools.report.parser.json.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;


public class MatchDeserializer extends JsonDeserializer<String> {

	private static final String LOCATION_FIELD = "location";
	
	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		JsonNode node = p.getCodec().readTree(p);
		
		if (node.has(LOCATION_FIELD)) {
			return node.get(LOCATION_FIELD).textValue();
		}
		
		return null;
	}
	
}
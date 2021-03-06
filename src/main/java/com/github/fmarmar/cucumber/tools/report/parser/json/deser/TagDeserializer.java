package com.github.fmarmar.cucumber.tools.report.parser.json.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;


public class TagDeserializer extends JsonDeserializer<String> {
	

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		JsonNode node = p.getCodec().readTree(p);
		
		return node.get("name").textValue();
	}
	
}
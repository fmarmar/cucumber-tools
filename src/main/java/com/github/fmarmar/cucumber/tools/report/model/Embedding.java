package com.github.fmarmar.cucumber.tools.report.model;

import java.nio.file.Path;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.fmarmar.cucumber.tools.report.parser.json.deser.EmbeddingDeserializer;
import com.google.common.net.MediaType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonDeserialize(using = EmbeddingDeserializer.class)
public class Embedding {

	private MediaType mimeType;
	
	private Path embeddingFile; 
	
	public String getMimeType() {
		return mimeType.toString();
	}
	
	public String getFilename() {
		return embeddingFile.getFileName().toString();
	}
	
}

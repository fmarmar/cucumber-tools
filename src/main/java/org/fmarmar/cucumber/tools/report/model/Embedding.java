package org.fmarmar.cucumber.tools.report.model;

import java.nio.file.Path;

import org.fmarmar.cucumber.tools.report.json.deser.EmbeddingDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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

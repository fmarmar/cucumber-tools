package org.fmarmar.cucumber.tools.report.parser.json.deser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.fmarmar.cucumber.tools.report.model.Embedding;
import org.fmarmar.cucumber.tools.report.parser.ReportParser.ParserConfiguration;
import org.fmarmar.cucumber.tools.report.utils.ReportUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.BaseEncoding;
import com.google.common.net.MediaType;


public class EmbeddingDeserializer extends BaseDeserializer<Embedding> {

	@Override
	public Embedding deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		JsonNode node = p.getCodec().readTree(p);

		MediaType mimeType = MediaType.parse(node.get("mime_type").textValue());
		Path data = writeDataToFile(node.get("data").textValue(), mimeType.withoutParameters(), getConfiguration(ctxt));

		return new Embedding(mimeType, data);
	}

	private Path writeDataToFile(String data, MediaType mimeType, ParserConfiguration configuration) throws IOException {

		Path baseDir = configuration.getEmbeddingDirectory();
		Path file = baseDir.resolve(ReportUtils.hash(data) + extension(mimeType));

		if (!file.toFile().exists()) {
			try (Reader dataReader = new StringReader(data)) {
				Files.copy(BaseEncoding.base64().decodingStream(dataReader), file);
			}
		}

		return file;
	}

	private String extension(MediaType mimeType) {
		
		if (MediaType.PLAIN_TEXT_UTF_8.is(mimeType)) {
			return ".txt";
		}
		
		if (mimeType.is(MediaType.SVG_UTF_8)) {
			return ".svg";
		}

		if (mimeType.is(MediaType.ANY_IMAGE_TYPE) || mimeType.is(MediaType.ANY_TEXT_TYPE)) {
			return '.' + mimeType.subtype();
		}

		if (MediaType.JSON_UTF_8.is(mimeType)) {
			return ".json";
		}
		
		if (MediaType.PDF.is(mimeType)) {
			return ".pdf";
		}

		return "unknown";
	}

}
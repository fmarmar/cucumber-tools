package com.github.fmarmar.cucumber.tools.report.parser;

import java.nio.file.Path;
import java.util.List;

import com.github.fmarmar.cucumber.tools.report.model.Feature;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ParsedReports {
	
	private final List<Feature> features;
	
	private final Path embeddingsDirectory;
	
}
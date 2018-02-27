package com.github.fmarmar.cucumber.tools.report.html.report;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.fmarmar.cucumber.tools.report.model.Scenario;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString 
@EqualsAndHashCode
@RequiredArgsConstructor
public class TagsReport {
	
	private final SortedMap<String, TagSummary> tagIndex = new TreeMap<>();
	
	public void collectTagsInfo(Iterable<String> tags, Scenario scenario) {
		
		for (String tag : tags) {
			
			TagSummary summary = tagIndex.get(tag);
			
			if (summary == null) {
				summary = new TagSummary(tag);
				tagIndex.put(tag, summary);
			}
			
			summary.collectScenarioInfo(scenario);
			
		}
		
	}
	
	public Collection<TagSummary> getTags() {
		return tagIndex.values();
	}
	
}

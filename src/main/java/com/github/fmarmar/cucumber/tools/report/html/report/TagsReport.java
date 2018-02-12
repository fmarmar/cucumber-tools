package com.github.fmarmar.cucumber.tools.report.html.report;

import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.fmarmar.cucumber.tools.report.model.Scenario;

public class TagsReport {
	
	private SortedMap<String, TagSummary> tagIndex = new TreeMap<>();

	public void collectTagsInfo(Set<String> tags, Scenario scenario) {
		
		for (String tag : tags) {
			
			TagSummary summary;
			
			if (tagIndex.containsKey(tag)) {
				summary = tagIndex.get(tag);
			} else {
				summary = new TagSummary(tag);
				tagIndex.put(tag, new TagSummary(tag));
			}
			
			summary.collectScenarioInfo(scenario);
			
		}
		
	}
	
	public Collection<String> getTagNames() {
		return tagIndex.keySet();
	}

	public Collection<TagSummary> getTags() {
		return tagIndex.values();
	}
		
}

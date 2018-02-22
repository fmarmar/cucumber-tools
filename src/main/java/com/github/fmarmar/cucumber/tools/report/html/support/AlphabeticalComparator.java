package com.github.fmarmar.cucumber.tools.report.html.support;

import java.util.Comparator;

import com.github.fmarmar.cucumber.tools.report.model.support.ComparableElement;

public class AlphabeticalComparator implements Comparator<ComparableElement> {

	public static final Comparator<ComparableElement> INSTANCE = new AlphabeticalComparator();
	
	private AlphabeticalComparator() { }
	
	@Override
	public int compare(ComparableElement named1, ComparableElement named2) {
		return named1.comparable().compareToIgnoreCase(named2.comparable());
	}
	
}
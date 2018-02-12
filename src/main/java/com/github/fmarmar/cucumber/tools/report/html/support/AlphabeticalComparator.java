package com.github.fmarmar.cucumber.tools.report.html.support;

import java.util.Comparator;

import com.github.fmarmar.cucumber.tools.report.model.support.NamedElement;

public class AlphabeticalComparator implements Comparator<NamedElement> {

	public static final Comparator<NamedElement> INSTANCE = new AlphabeticalComparator();
	
	private AlphabeticalComparator() { }
	
	@Override
	public int compare(NamedElement named1, NamedElement named2) {
		return named1.getName().compareToIgnoreCase(named2.getName());
	}
	
}
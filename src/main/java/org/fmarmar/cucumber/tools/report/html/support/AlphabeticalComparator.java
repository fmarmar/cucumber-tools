package org.fmarmar.cucumber.tools.report.html.support;

import java.util.Comparator;

import org.fmarmar.cucumber.tools.report.model.support.NamedElement;

public class AlphabeticalComparator implements Comparator<NamedElement> {

	public static final Comparator<NamedElement> INSTANCE = new AlphabeticalComparator();
	
	private AlphabeticalComparator() { }
	
	@Override
	public int compare(NamedElement named1, NamedElement named2) {
		return named1.getName().compareTo(named2.getName());
	}
	
}
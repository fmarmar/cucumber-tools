package com.github.fmarmar.cucumber.tools.split;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;


public class SplitUtils {
	
	private SplitUtils() {}
	
	public static <T> List<List<T>> splitList(List<T> list, int split) {
		
		if (list.size() < split) {
			return Collections.singletonList(list);
		}
		
		return Lists.partition(list, (int) Math.ceil(((double) list.size()) / split));
	}
	
}
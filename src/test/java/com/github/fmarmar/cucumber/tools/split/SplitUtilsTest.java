package com.github.fmarmar.cucumber.tools.split;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class SplitUtilsTest {
	
	@SuppressWarnings("unused")
	private Object splitListTests() {
		return new Object[][] {
				{4, 2},
				{30, 3},
				{1, 2},
				{5, 2},
				{3, 2},
				{100, 3}
		};
	}
	
	@Test
	@Parameters(method = "splitListTests")
	public void testSplitList(int initialSize, int split) {
		
		List<String> list = buildList(initialSize);
		
		assertThat(SplitUtils.splitList(list, split)).hasSize((initialSize >= split) ? split : initialSize);
		
	}

	private List<String> buildList(int size) {
		
		List<String> list = new ArrayList<>();
		
		while (list.size() < size) {
			list.add("a");
		}
		
		return list;
	}
	
	
	
}
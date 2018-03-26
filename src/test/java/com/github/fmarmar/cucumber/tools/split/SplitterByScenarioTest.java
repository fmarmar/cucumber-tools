package com.github.fmarmar.cucumber.tools.split;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.fmarmar.cucumber.tools.TestUtils;
import com.github.fmarmar.cucumber.tools.jcommander.TagExpressionConverter;
import com.github.fmarmar.cucumber.tools.split.SplitterByScenario.PickleInfo;

import io.cucumber.tagexpressions.Expression;
import io.cucumber.tagexpressions.TagExpressionParser;

public class SplitterByScenarioTest {
	
	private SplitterByScenario splitter;
	
	@Before
	public void configure() {
		splitter = new SplitterByScenario();
	}
	
	@Test
	public void testSplitter() throws IOException {
		
		List<PickleInfo> pickles = splitter.split(TestUtils.SPLIT_FEATURES_BASE_PATH, TagExpressionConverter.NO_EXPRESSION);
		
		assertThat(pickles).size().isEqualTo(3);
	}
	
	@Test
	public void testSplitterWithTags() throws IOException {
		
		List<PickleInfo> pickles = splitter.split(TestUtils.SPLIT_FEATURES_BASE_PATH, parse("@scenarioOutlineTag"));
		
		assertThat(pickles).size().isEqualTo(2);
	}
	
	@Test
	public void testSplitterWithFeatureTags() throws IOException {
		
		List<PickleInfo> pickles = splitter.split(TestUtils.SPLIT_FEATURES_BASE_PATH, parse("@aFeatureTag and not @scenarioOutlineTag"));
		
		assertThat(pickles).size().isEqualTo(1);
	}
	
	
	private static Expression parse(String str) {
		return new TagExpressionParser().parse(str);
	}
}
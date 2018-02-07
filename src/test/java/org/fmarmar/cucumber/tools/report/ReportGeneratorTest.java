package org.fmarmar.cucumber.tools.report;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import org.fmarmar.cucumber.tools.report.html.ReportGenerator;
import org.fmarmar.cucumber.tools.report.html.page.PageGenerator;
import org.fmarmar.cucumber.tools.report.html.page.PageGenerator.PageId;
import org.fmarmar.cucumber.tools.report.html.report.Failure;
import org.fmarmar.cucumber.tools.report.model.Feature;
import org.fmarmar.cucumber.tools.report.model.FeatureBuilder;
import org.fmarmar.cucumber.tools.report.model.support.GenericStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.collections.Iterables;

import com.google.common.collect.Lists;

public class ReportGeneratorTest {
	
	private ReportGenerator reportGenerator;
	
	private PageGenerator pageGenerator;
	
	@Before
	public void configureTest() throws IOException {
		
		Path tmpPath = Files.createTempDirectory("test-");
		pageGenerator = basicPageGeneratorMock(tmpPath);
		
		reportGenerator = new ReportGenerator(pageGenerator, tmpPath, 1);
		
	}
	
	private PageGenerator basicPageGeneratorMock(Path tmpPath) {
		
		PageGenerator generator = mock(PageGenerator.class);
		when(generator.resolvePagePath(any(PageId.class))).thenReturn(tmpPath);
		when(generator.resolvePagePath(any(PageId.class), any(String.class))).thenReturn(tmpPath);
				
		return generator;
	}
	
	@Test
	public void testNoFailuresReport() throws IOException, InterruptedException {
		
		Feature feature = FeatureBuilder.newFeature()
				.withScenarios(GenericStatus.PASSED, GenericStatus.PASSED, GenericStatus.SKIPPED)
				.build();
		
		reportGenerator.generateReport(Lists.newArrayList(feature));
		reportGenerator.finishReport();
		
		Collection<Failure> failures = getModelObject(PageId.FAILURES_OVERVIEW, ReportGenerator.FAILURES_KEY);
		
		assertThat(failures, empty());
		
	}
	
	@Test
	public void testFailuresReport() throws IOException, InterruptedException {
		
		Feature feature = FeatureBuilder.newFeature()
				.withScenarios(GenericStatus.PASSED, GenericStatus.FAILED, GenericStatus.SKIPPED)
				.build();
		
		reportGenerator.generateReport(Lists.newArrayList(feature));
		reportGenerator.finishReport();
		
		Collection<Failure> failures = getModelObject(PageId.FAILURES_OVERVIEW, ReportGenerator.FAILURES_KEY);
		
		assertThat(failures, hasSize(1));
		assertThat(Iterables.firstOf(failures).getScenarios(), hasSize(1));
		
	}
	
	@Test
	public void testFailedScenariosAreGroupedByFeatureInFailuresReport() throws IOException, InterruptedException {
		
		Feature feature = FeatureBuilder.newFeature()
				.withScenarios(GenericStatus.PASSED, GenericStatus.FAILED, GenericStatus.FAILED)
				.build();
		
		reportGenerator.generateReport(Lists.newArrayList(feature));
		reportGenerator.finishReport();
		
		Collection<Failure> failures = getModelObject(PageId.FAILURES_OVERVIEW, ReportGenerator.FAILURES_KEY);
		
		assertThat(failures, hasSize(1));
		assertThat(Iterables.firstOf(failures).getScenarios(), hasSize(2));
		
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getModelObject(PageId pageId, String key) throws IOException {
		
		ArgumentCaptor<Map<String, Object>> modelCaptor = ArgumentCaptor.forClass(Map.class);
		verify(pageGenerator, atLeastOnce()).generatePage(eq(pageId), any(Path.class), modelCaptor.capture());
			
		return (T) modelCaptor.getValue().get(key);
	}
	
	
	
	
	
	
}
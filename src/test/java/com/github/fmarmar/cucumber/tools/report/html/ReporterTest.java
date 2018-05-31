package com.github.fmarmar.cucumber.tools.report.html;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assume.assumeThat;
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

import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.collections.Iterables;

import com.github.fmarmar.cucumber.tools.report.html.page.PageGenerator;
import com.github.fmarmar.cucumber.tools.report.html.page.PageGenerator.PageId;
import com.github.fmarmar.cucumber.tools.report.html.report.Failure;
import com.github.fmarmar.cucumber.tools.report.html.report.TagsReport;
import com.github.fmarmar.cucumber.tools.report.model.Feature;
import com.github.fmarmar.cucumber.tools.report.model.FeatureBuilder;
import com.github.fmarmar.cucumber.tools.report.model.support.GenericStatus;
import com.google.common.collect.Lists;

public class ReporterTest {

	private Reporter reporter;

	private PageGenerator pageGenerator;

	@Before
	public void checkJava8Runtime() {
		assumeThat(System.getProperty("java.version"), not(startsWith("1.7")));
	}

	@Before
	public void configureTest() throws IOException {

		Path tmpPath = Files.createTempDirectory("test-");
		pageGenerator = basicPageGeneratorMock(tmpPath);

		reporter = new Reporter(pageGenerator, tmpPath, 1);

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

		reporter.generateReport(Lists.newArrayList(feature));
		reporter.finishReport();

		Collection<Failure> failures = getModelObject(PageId.FAILURES_OVERVIEW, Reporter.FAILURES_KEY);

		assertThat(failures).isEmpty();

	}

	@Test
	public void testFailuresReport() throws IOException, InterruptedException {

		Feature feature = FeatureBuilder.newFeature()
				.withScenarios(GenericStatus.PASSED, GenericStatus.FAILED, GenericStatus.SKIPPED)
				.build();

		reporter.generateReport(Lists.newArrayList(feature));
		reporter.finishReport();

		Collection<Failure> failures = getModelObject(PageId.FAILURES_OVERVIEW, Reporter.FAILURES_KEY);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			softly.assertThat(failures).size().isEqualTo(1);
			softly.assertThat(Iterables.firstOf(failures).getScenarios()).size().isEqualTo(1);
		}

	}

	@Test
	public void testTagsReport() throws IOException, InterruptedException {

		Feature feature = FeatureBuilder.newFeature()
				.withRandoScenarios(10)
				.build();

		reporter.generateReport(Lists.newArrayList(feature));
		reporter.finishReport();

		TagsReport tagsReport = getModelObject(PageId.TAGS_OVERVIEW, Reporter.TAGS_REPORT_KEY);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {

			softly.assertThat(tagsReport).isNotNull();

			softly.assertThat(tagsReport.getTags()).size().isGreaterThan(0);

		}

	}

	@Test
	public void testFailedScenariosAreGroupedByFeatureInFailuresReport() throws IOException, InterruptedException {

		Feature feature = FeatureBuilder.newFeature()
				.withScenarios(GenericStatus.PASSED, GenericStatus.FAILED, GenericStatus.FAILED)
				.build();

		reporter.generateReport(Lists.newArrayList(feature));
		reporter.finishReport();

		Collection<Failure> failures = getModelObject(PageId.FAILURES_OVERVIEW, Reporter.FAILURES_KEY);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			softly.assertThat(failures).size().isEqualTo(1);
			softly.assertThat(Iterables.firstOf(failures).getScenarios()).size().isEqualTo(2);
		}

	}

	@SuppressWarnings("unchecked")
	private <T> T getModelObject(PageId pageId, String key) throws IOException {

		ArgumentCaptor<Map<String, Object>> modelCaptor = ArgumentCaptor.forClass(Map.class);
		verify(pageGenerator, atLeastOnce()).generatePage(eq(pageId), any(Path.class), modelCaptor.capture());

		return (T) modelCaptor.getValue().get(key);
	}






}
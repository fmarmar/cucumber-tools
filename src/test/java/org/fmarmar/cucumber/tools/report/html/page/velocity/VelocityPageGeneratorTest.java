package org.fmarmar.cucumber.tools.report.html.page.velocity;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.velocity.runtime.RuntimeConstants;
import org.fmarmar.cucumber.tools.report.html.page.PageGenerator.PageId;
import org.fmarmar.cucumber.tools.report.html.support.ReportMetadata;
import org.junit.Before;
import org.junit.Test;

public class VelocityPageGeneratorTest {

	private VelocityPageGenerator generator;

	@Before
	public void configureTest() throws IOException {
		generator = new VelocityPageGenerator(new ReportMetadata(), RuntimeConstants.NUMBER_OF_PARSERS);
	}

	@Test
	public void testResolvePagePathByPageIdImplementsAllValues() {

		for (PageId pageId : PageId.values()) {
			if (pageId.summaryPage) {
				try {
					generator.resolvePagePath(pageId);
				} catch (IllegalArgumentException e) {
					fail("PageId " + pageId + " not considered");
				}
			}
		}

	}

	@Test
	public void testResolvePagePathByPageIdAndNameImplementsAllValues() {

		for (PageId pageId : PageId.values()) {
			try {
				generator.resolvePagePath(pageId, "aName");

			} catch (IllegalArgumentException e) {
				fail("PageId " + pageId + " not considered");
			}
		}

	}

}
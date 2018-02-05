package org.fmarmar.cucumber.tools.report.html.page;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface PageGenerator {
	
	enum PageId {
		FEATURES_OVERVIEW,
		FEATURE,
		TAGS_OVERVIEW
	}
	
	void initialize(Path output) throws IOException;
	
	void copyStaticResources(Path output) throws IOException;
	
	Path resolvePagePath(PageId pageId);
	
	Path resolvePagePath(PageId pageId, String name);

	void generatePage(PageId pageId, Path page, Map<String, Object> model) throws IOException;

}
package org.fmarmar.cucumber.tools.report.html.page.velocity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.fmarmar.cucumber.tools.report.model.ExecutionElement;
import org.fmarmar.cucumber.tools.report.model.Metadata;
import org.fmarmar.cucumber.tools.report.model.support.ScenarioResult;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public final class TemplateUtils {
	
	private static final String[] METADATA_LABELS = {"os", "language", "browser", "device"};
	
	private static final TimeUnit NANOSECONDS = TimeUnit.NANOSECONDS;
	
	public static final TemplateUtils INSTANCE = new TemplateUtils();
	
	private static final Cache<Metadata, String> METADATA_TOOLTIP_CACHE = CacheBuilder.newBuilder().maximumSize(20).build();

	private TemplateUtils() { }
	
	public String validFileName(String fileName) {
		return fileName.replaceAll("[\\W]+", "-");
	}

	public String tagFileName(String tag) {
		return validFileName(tag.replace("@", "")) + ".html";
	}

	public String formatDuration(long duration) {
		
		long remaining = duration;
		
		long hours = TimeUnit.HOURS.convert(remaining, NANOSECONDS);
		remaining = remaining - TimeUnit.HOURS.toNanos(hours);
		
		long minutes = TimeUnit.MINUTES.convert(remaining, NANOSECONDS);
		remaining = remaining - TimeUnit.MINUTES.toNanos(minutes);
		
		long seconds = TimeUnit.SECONDS.convert(remaining, NANOSECONDS);
		remaining = remaining - TimeUnit.SECONDS.toNanos(seconds);
		
		long milliSeconds = TimeUnit.MILLISECONDS.convert(remaining, NANOSECONDS);
				
		StringBuilder formatted = new StringBuilder(20);
		
		if (hours > 0) {
			formatted.append(hours).append("h ");
		}
		
		if (minutes > 0) {
			formatted.append(minutes).append("m ");
		}
		
		if (seconds > 0) {
			formatted.append(seconds).append("s ");
		}
		
		if (milliSeconds >= 0) {
			String value = String.valueOf(milliSeconds);
			formatted.append(Strings.padStart(value, 3, '0')).append("ms");
		}
		
		return formatted.toString(); 
	    
	}
	
	public ScenarioResult result(Iterable<ExecutionElement> scenarioElements) {
		return ScenarioResult.result(scenarioElements);
	}

	public String percentage(long value, long total) {
		float percentage = (total == 0) ? 0 : (Float.valueOf(value) / total);
		return newPercentFormatter().format(percentage);
	}
	
	private NumberFormat newPercentFormatter() {
		
		NumberFormat formatter = NumberFormat.getPercentInstance(Locale.US);
		formatter.setMinimumFractionDigits(2);
		formatter.setMaximumFractionDigits(2);
		
		return formatter;
	}
	
	public String messageSummary(String message) {
		return message.split("[\\r\\n]+")[0];
	}
	
	public String metadataTooltip(Metadata metadata) {
		
		String tooltip = METADATA_TOOLTIP_CACHE.getIfPresent(metadata);
		
		if (tooltip == null) {
			tooltip = buildMetadataTooltip(metadata);
			METADATA_TOOLTIP_CACHE.put(metadata, tooltip);
		}
		
		return tooltip;
		
	}

	private String buildMetadataTooltip(Metadata metadata) {
		
		String[] metadataValues = {metadata.getOs(), metadata.getLanguage(), metadata.getBrowser(), metadata.getDevice()};
		List<String> tooltipLines = new ArrayList<>(5);
		
		for (int idx=0; idx<metadataValues.length; idx++) {
			if (!Strings.isNullOrEmpty(metadataValues[idx])) {
				tooltipLines.add(METADATA_LABELS[idx] + " = " + metadataValues[idx]);
			}
		}
		
		return Joiner.on("&#10;").join(tooltipLines);
	}

}
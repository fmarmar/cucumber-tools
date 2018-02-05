package org.fmarmar.cucumber.tools.report.html.page.velocity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.fmarmar.cucumber.tools.report.model.ExecutionElement;
import org.fmarmar.cucumber.tools.report.model.support.ScenarioResult;

public final class TemplateUtils {

	// provide Locale so tests can validate . (instead of ,) separator
	public static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance(Locale.US);
	
	private static final TimeUnit NANOSECONDS = TimeUnit.NANOSECONDS;

	static {
		PERCENT_FORMATTER.setMinimumFractionDigits(2);
		PERCENT_FORMATTER.setMaximumFractionDigits(2);
	}

	private static final NumberFormat DECIMAL_FORMATTER = DecimalFormat.getInstance(Locale.US);

	static {
		DECIMAL_FORMATTER.setMinimumFractionDigits(2);
		DECIMAL_FORMATTER.setMaximumFractionDigits(2);
	}
	
	private static final NumberFormat MILLISECONDS_FORMATTER = DecimalFormat.getInstance(Locale.US);

	public static final TemplateUtils INSTANCE = new TemplateUtils();

	//    private static final PeriodFormatter TIME_FORMATTER = new PeriodFormatterBuilder()
	//            .appendHours()
	//            .appendSeparator(":")
	//            .appendMinutes()
	//            .appendSeparator(":")
	//            .printZeroAlways()
	//            .appendSeconds()
	//            .appendSeparator(".")
	//            .minimumPrintedDigits(3)
	//            .appendMillis()
	//            .toFormatter();

	private TemplateUtils() { }
	
	public String tagFileName(String tag) {
		// FIXME escape chars like : > ...
		return StringUtils.substringAfter(tag, "@") + ".html";
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
			formatted.append(StringUtils.leftPad(value, 3, '0')).append("ms");
		}
		
		return formatted.toString(); 
	    
	}
	
	public ScenarioResult result(Iterable<ExecutionElement> scenarioElements) {
		return ScenarioResult.result(scenarioElements);
	}

	/**
	 * Returns value converted to percentage format.
	 *
	 * @param value value to convert
	 * @param total sum of all values
	 * @return converted values including '%' character
	 */
	public static String formatAsPercentage(int value, int total) {
		// value '1F' is to force floating conversion instead of loosing decimal part
		float average = total == 0 ? 0 : 1F * value / total;
		return PERCENT_FORMATTER.format(average);
	}

	public static String formatAsDecimal(int value, int total) {
		float average = total == 0 ? 0 : 100F * value / total;
		return DECIMAL_FORMATTER.format(average);
	}

	/**
	 * Converts characters of passed string by replacing to dash (-) each character that might not be accepted as file
	 * name such as / ? or &gt;.
	 *
	 * @param fileName
	 *            sequence that should be converted
	 * @return converted string
	 */
	public static String toValidFileName(String fileName) {
		return StringEscapeUtils.escapeJava(fileName).replaceAll("[^\\d\\w]", "-");
	}
}
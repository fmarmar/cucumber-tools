package com.github.fmarmar.cucumber.tools.report.html.page.velocity;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * Escapes all html and xml that was provided in a reference before inserting it into a template.
 *
 * References that start with $_sanitize_ will be sanitized to allow urls.
 * REferences from TemplateUtils methods ($utils.) will not be escaped
 */
public final class EscapeHtmlReference implements ReferenceInsertionEventHandler {

    private static final PolicyFactory LINKS = new HtmlPolicyBuilder()
            .allowStandardUrlProtocols().allowElements("a").allowAttributes("href")
            .onElements("a").requireRelNofollowOnLinks().requireRelsOnLinks("noopener", "noreferrer")
            .toFactory();

    @Override
	public Object referenceInsert(Context context, String reference, Object value) {
		
		if (value == null) {
            return null;
        }
		
		if (reference.startsWith("$_sanitize_")) {
            return LINKS.sanitize(value.toString());
        }
		
		if (reference.startsWith("$utils.")) {
            return value.toString();
        }
		
		return StringEscapeUtils.escapeHtml4(value.toString());
        
	}

}

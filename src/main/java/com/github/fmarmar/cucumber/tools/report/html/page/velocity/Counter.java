package com.github.fmarmar.cucumber.tools.report.html.page.velocity;

import org.apache.commons.lang3.mutable.MutableInt;

/**
 * Simple counter to give elements on a page a unique ID. Using object hashes
 * doesn't guarantee uniqueness.
 */
public class Counter extends MutableInt {
    
	private static final long serialVersionUID = 6030846356522495058L;

	/**
     * @return The next integer
     */
    public int next() {
        increment();
        return intValue();
    }
}

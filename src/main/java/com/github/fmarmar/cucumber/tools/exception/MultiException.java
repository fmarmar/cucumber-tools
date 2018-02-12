package com.github.fmarmar.cucumber.tools.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

public class MultiException extends RuntimeException {

	private static final long serialVersionUID = 3422623024730836768L;

	private final Collection<Throwable> exceptions;

	private MultiException(String message, Throwable th) {
		super(message, th);
		this.exceptions = Collections.emptyList();
	}

	private MultiException(String message, Collection<Throwable> errors) {
		super(message);
		this.exceptions = errors;
	}

	@Override
	public synchronized Throwable initCause(Throwable cause) {
		throw new IllegalArgumentException("Can't overwrite cause", this);
	}

	@Override
	public void printStackTrace() {

		if (exceptions.isEmpty()) {
			super.printStackTrace();
		} else {
			for (Throwable error : exceptions) {
				error.printStackTrace();
			}
		}

	}

	@Override
	public void printStackTrace(PrintStream s) {
		
		if (exceptions.isEmpty()) {
			super.printStackTrace(s);
		} else {
			for (Throwable error : exceptions) {
				error.printStackTrace(s);
			}
		}
		
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		
		if (exceptions.isEmpty()) {
			super.printStackTrace(s);
		} else {
			for (Throwable error : exceptions) {
				error.printStackTrace(s);
			}
		}
		
	}

	public static MultiException newInstance(Collection<Throwable> exceptions) {
		return newInstance(null, exceptions);
	}
	
	public static MultiException newInstance(String msg, Collection<Throwable> exceptions) {

		if (exceptions.isEmpty()) {
			throw new IllegalArgumentException("Can't create MultiException with an empty collection");
		}

		if (exceptions.size() == 1) {
			
			Throwable th = Iterables.getOnlyElement(exceptions);
			return new MultiException(buildMessage(msg, th), th);
		}
		
		return new MultiException(buildMessage(msg, exceptions), exceptions);
	}
	
	private static String buildMessage(String msg, Throwable th) {
		return (Strings.isNullOrEmpty(msg)) ? th.getMessage() : msg;
	}
	
	private static String buildMessage(String msg, Collection<Throwable> exceptions) {
		
		StringBuilder finalMessage = new StringBuilder(100);
		
		if (!Strings.isNullOrEmpty(msg)) {
			finalMessage.append(msg);
		}
		
		finalMessage.append("Multiple exceptions thrown: ");
		
		// If multiple exceptions were thrown, keep track the unique exception messages
		Set<String> exceptionMsgs = new LinkedHashSet<>();
		
		for (Throwable e : exceptions) {
			exceptionMsgs.add(e.toString());
        }
		
		Joiner.on(',').appendTo(finalMessage, exceptionMsgs);
		
		return finalMessage.toString();
	}

}
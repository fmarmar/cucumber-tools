package org.fmarmar.cucumber.tools.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;

public class MultiException extends RuntimeException {
	
	private static final long serialVersionUID = 3422623024730836768L;
	
	private final Collection<Throwable> errors;
	
	public MultiException(Collection<Throwable> errors) {
		this("Multiple errors", errors);
	}

	public MultiException(String message, Collection<Throwable> errors) {
		super(message);
		this.errors = errors;
	}
	
	@Override
	public synchronized Throwable initCause(Throwable cause) {
        throw new IllegalArgumentException("Can't overwrite cause", this);
    }
	
	@Override
	public void printStackTrace() {
		for (Throwable error : errors) {
			error.printStackTrace();
		}
	}
	
	@Override
	public void printStackTrace(PrintStream s) {
		for (Throwable error : errors) {
        	error.printStackTrace(s);
		}
    }
	
	@Override
	public void printStackTrace(PrintWriter s) {
		for (Throwable error : errors) {
        	error.printStackTrace(s);
		}
    }

	
}
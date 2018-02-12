package com.github.fmarmar.cucumber.tools.exception;



public class CommandException extends RuntimeException {
	
	private static final long serialVersionUID = -1421267674390174538L;

	public CommandException(String message) {
		super(message);
	}
	
	public CommandException(Throwable th) {
		super(th);
	}
	
	public CommandException(String message, Throwable th) {
		super(message, th);
	}
	
}
package com.mnn.society.server.bamul.utils;

public class BAMULInvalidOpException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4974471622399325834L;
	
	private String message;

	public BAMULInvalidOpException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}

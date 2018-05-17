package com.intuit.cg.backendtechassessment.exceptions;

/**
 * Thrown whenever a Bid cannot be found.
 */
public class NoBidFoundException extends Exception {
	public NoBidFoundException(String msg) {
		super(msg);
	}
}
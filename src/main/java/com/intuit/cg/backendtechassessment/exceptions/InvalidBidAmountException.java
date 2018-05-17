package com.intuit.cg.backendtechassessment.exceptions;

/**
 * Thrown whenever an invalid bid amount is detected.
 */
public class InvalidBidAmountException extends Exception {
	public InvalidBidAmountException(String msg) {
		super(msg);
	}
}
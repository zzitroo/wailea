package com.intuit.cg.backendtechassessment.utils;

import java.text.DecimalFormat;
import java.text.FieldPosition;

/**
 * Utility class to make nice US currency strings out of raw double values. Provides
 * "nearest neighbor" rounding mode - i.e. half-even
 *
 * Call this using: CurrencyFormatter.makePrettyCurrencyString(rawDouble)
 */
public class CurrencyFormatter {
	private static final DecimalFormat FORMAT = new DecimalFormat("#0.00");
	
	private CurrencyFormatter() {
	}
	
	public static String makePrettyCurrencyString(double rawDouble) {
		StringBuffer buffy = new StringBuffer("$");
		FORMAT.format(rawDouble, buffy, new FieldPosition(0));
		return buffy.toString();
	}
}
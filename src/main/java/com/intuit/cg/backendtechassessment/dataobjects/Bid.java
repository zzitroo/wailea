package com.intuit.cg.backendtechassessment.dataobjects;

import java.util.UUID;
import lombok.Data;

/**
 * Defines a single bid. The bid can only be linked to a single buyer. Valid bid amounts
 * must be greater than $0.00. Attempting to set a negative bid amount will return an
 * exception. A bid can only be linked to a single project.
 */
public @Data class Bid {
	private UUID buyerId;
	private double bidAmount;
	private UUID projectId;
	private UUID bidId;
}
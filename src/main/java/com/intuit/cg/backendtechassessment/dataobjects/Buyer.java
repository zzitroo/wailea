package com.intuit.cg.backendtechassessment.dataobjects;

import java.util.UUID;
import lombok.Data;

/**
 * Defines a single buyer. The buyerId is unique (UUID). If the autobidAmount is set,
 * then the system should automatically include a bid for this buyer for all projects.
 */
public @Data class Buyer {
	private String name;
	private UUID buyerId;
	private double autobidAmount;
	private boolean autobidSet;
}
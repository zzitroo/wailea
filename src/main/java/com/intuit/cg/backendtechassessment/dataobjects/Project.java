package com.intuit.cg.backendtechassessment.dataobjects;

import java.util.UUID;
import lombok.Data;

/**
 * Defines a single project. The winning bid is always min(List<Bid>) when current date
 * is greater than projectEndDate. The project links to the seller by id.
 */
public @Data class Project {
	private String title;
	private UUID projectId;
	private String description;
	private double maxBudget;
	private long projectEndDate;	// in milliseconds since epoch
	private UUID sellerId;
	private Bid selectedBid;
	private Buyer selectedBuyer;	// could be empty if no buyer has bid on the project
}
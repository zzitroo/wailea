package com.intuit.cg.backendtechassessment.dataobjects;

import java.util.UUID;
import lombok.Data;

/**
 * Defines a single seller. The sellerId is unique (UUID)
 */
public @Data class Seller {
	private String name;
	private UUID sellerId;
}
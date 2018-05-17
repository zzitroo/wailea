package com.intuit.cg.backendtechassessment.dataobjects;

import java.util.Collection;
import lombok.Data;

/**
 * Defines a collection of projects.
 */
public @Data class Projects {
	private Collection<Project> projects;
}
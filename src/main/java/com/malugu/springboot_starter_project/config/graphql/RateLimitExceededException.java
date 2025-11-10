package com.malugu.springboot_starter_project.config.graphql;

import graphql.execution.AbortExecutionException;

public class RateLimitExceededException extends AbortExecutionException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1860869610689165242L;

	public RateLimitExceededException(String message) {
		super(message);
	}

}

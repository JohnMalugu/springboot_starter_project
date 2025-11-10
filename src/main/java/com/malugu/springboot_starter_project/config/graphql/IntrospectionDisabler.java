package com.malugu.springboot_starter_project.config.graphql;

import graphql.ExecutionResult;
import graphql.execution.AbortExecutionException;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class IntrospectionDisabler implements Instrumentation {

	private final RateLimitConfig config;

	@Override
	public InstrumentationState createState() {
		return new InstrumentationState() {
		};
	}

	@Override
	public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult,
			InstrumentationExecutionParameters parameters) {
		if (!config.getIntrospection().isEnabled() && isIntrospectionQuery(parameters.getQuery())) {
			return CompletableFuture.failedFuture(new AbortExecutionException("Introspection is disabled"));
		}
		return CompletableFuture.completedFuture(executionResult);
	}

	private boolean isIntrospectionQuery(String query) {
		if (query == null)
			return false;
		// Simple check for common introspection patterns
		return query.contains("__schema") || query.contains("__type") || query.contains("__typename");
	}
}
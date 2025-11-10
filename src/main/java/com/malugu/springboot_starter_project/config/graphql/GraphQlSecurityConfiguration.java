package com.malugu.springboot_starter_project.config.graphql;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.AbortExecutionException;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.SimplePerformantInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.schema.GraphQLSchema;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class GraphQlSecurityConfiguration {

	private final RateLimitConfig rateLimitConfig;
	private final GraphQLSchema graphQLSchema;

	@Bean
	public GraphQLRateLimiter graphQLRateLimiter() {
		return new GraphQLRateLimiter(rateLimitConfig);
	}

	@Bean
	public IntrospectionDisabler introspectionDisabler() {
		return new IntrospectionDisabler(rateLimitConfig);
	}

	@Bean
	public MaxQueryDepthInstrumentation depthInstrumentation() {
		return new MaxQueryDepthInstrumentation(rateLimitConfig.getDepth().getMaxDepth()); // Max depth of 10
	}

	@Bean
	public MaxQueryComplexityInstrumentation queryComplexityInstrumentation() {
		return new MaxQueryComplexityInstrumentation(rateLimitConfig.getDepth().getMaxComplexity()); // Max Complexity
																										// of 13
	}

	@Bean
	public GraphQL graphQL(GraphQLRateLimiter rateLimiter, IntrospectionDisabler introspectionDisabler,
			MaxQueryDepthInstrumentation depthInstrumentation,
			MaxQueryComplexityInstrumentation queryComplexityInstrumentation,
			@Qualifier("timeoutInstrumentation") Instrumentation timeoutInstrumentation) {
		// Chain them in execution order
		List<Instrumentation> instrumentations = Arrays.asList(depthInstrumentation, // 1. Depth limiting
				queryComplexityInstrumentation, // 2. Complexity analysis
				rateLimiter, // 3. Rate limiting
				introspectionDisabler, // 4. Introspection control
				timeoutInstrumentation() // 5.Query Execution Timeout
		);
		return GraphQL.newGraphQL(graphQLSchema).instrumentation(new ChainedInstrumentation(instrumentations))
				.build();
	}

	@Bean
	public Bucket bucket() {
		// Replaces the deprecated Bandwidth.classic()
		Bandwidth limit = Bandwidth.builder().capacity(100) // 100 requests
				.refillIntervally(100, Duration.ofHours(1)) // Refill 100 tokens every hour
				.build();

		return Bucket.builder().addLimit(limit).build();
	}

	// Timeout Enforcement
	@Qualifier("timeoutInstrumentation")
	@Bean
	public Instrumentation timeoutInstrumentation() {
		return new SimplePerformantInstrumentation() {
			@Override
			public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult,
					InstrumentationExecutionParameters parameters) {
				CompletableFuture<ExecutionResult> future = new CompletableFuture<>();
				future.orTimeout(rateLimitConfig.getExecution().getTimeout().toMillis(), TimeUnit.MILLISECONDS)
						.exceptionally(ex -> {
							if (ex.getCause() instanceof TimeoutException) {
								throw new AbortExecutionException("Execution timed out after 5 seconds");
							}
							return null;
						});

				return future;
			}
		};
	}


}

package com.malugu.springboot_starter_project.config.graphql;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.language.OperationDefinition;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class GraphQLRateLimiter implements Instrumentation {

	private final RateLimitConfig config;
	private final Bucket globalBucket;
	private final Bucket mutationBucket;
	private final Bucket ipBucket;

	public GraphQLRateLimiter(RateLimitConfig config) {
		this.config = config;
		this.globalBucket = createBucket(config.getGlobal().getCapacity(), config.getGlobal().getRefillAmount(),
				config.getGlobal().getRefillPeriod());
		this.mutationBucket = createBucket(config.getMutation().getCapacity(), config.getMutation().getRefillAmount(),
				config.getMutation().getRefillPeriod());
		this.ipBucket = createBucket(config.getIp().getCapacity(), config.getIp().getRefillAmount(),
				config.getIp().getRefillPeriod());
	}

	private Bucket createBucket(long capacity, long refillAmount, Duration refillPeriod) {
		return Bucket.builder()
            .addLimit(Bandwidth.builder()
						.capacity(capacity).refillIntervally(refillAmount, refillPeriod)
                .build())
            .build();
//		Duration duration = Duration.ofHours(2);
//		Instant.now().plus(duration);
//		return Bucket.builder()
//	            .addLimit(Bandwidth.builder()
//							.capacity(capacity).refillIntervally(refillAmount, Duration.ofDays(1))
//	                .build())
//	            .build();
	}

	@Override
	public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult,
			InstrumentationExecutionParameters parameters) {
		// Get operation type
		OperationDefinition.Operation operationType = getOperationType(parameters.getQuery());

		System.err.println(operationType);
		// IP-based rate limiting
		if (config.getIp().isEnabled()) {
			try {
				HttpServletRequest request = parameters.getGraphQLContext().get(HttpServletRequest.class);
				if (request != null && !ipBucket.tryConsume(1)) {
					return CompletableFuture.failedFuture(new RateLimitExceededException("IP rate limit exceeded"));
				}
			} catch (Exception e) {
				log.warn("IP rate limit check failed", e);
			}
		}

		// Global rate limiting
		if (config.getGlobal().isEnabled() && !globalBucket.tryConsume(1)) {
			return CompletableFuture.failedFuture(new RateLimitExceededException("Global rate limit exceeded"));
		}

		// Mutation-specific rate limiting
		if (config.getMutation().isEnabled() && operationType == OperationDefinition.Operation.MUTATION
				&& !mutationBucket.tryConsume(1)) {
			return CompletableFuture.failedFuture(new RateLimitExceededException("Mutation rate limit exceeded"));
		}

		return CompletableFuture.completedFuture(executionResult);
	}

	// Add to your RateLimiter class
	private OperationDefinition.Operation getOperationType(String query) {
		try {
			if (query == null || query.trim().isEmpty()) {
				return OperationDefinition.Operation.QUERY;
			}

			// Quick check before full parsing
			String normalized = query.trim().toUpperCase();
			if (normalized.startsWith("MUTATION")) {
				return OperationDefinition.Operation.MUTATION;
			}
			if (normalized.startsWith("SUBSCRIPTION")) {
				return OperationDefinition.Operation.SUBSCRIPTION;
			}

			// Full parse for complex cases
			return GraphQLOperationUtils.getOperationType(query);
		} catch (Exception e) {
			log.warn("Operation type detection failed for query: {}", query, e);
			return OperationDefinition.Operation.QUERY;
		}
	}

//	private OperationDefinition.Operation getOperationType(InstrumentationExecutionParameters parameters) {
//
//		String operationStr = parameters.getOperation().toString();
//		System.err.println(operationStr);
//		try {
//			System.err.println(parameters.getQuery());
//			return OperationDefinition.Operation.valueOf(operationStr);
//		} catch (IllegalArgumentException e) {
//
//			log.warn("Unknown operation type: {}", operationStr);
//			return OperationDefinition.Operation.QUERY; // Default to query
//		}
//	}

	// Other required Instrumentation methods
	@Override
	public InstrumentationState createState() {
		return new InstrumentationState() {
		};
    }

}
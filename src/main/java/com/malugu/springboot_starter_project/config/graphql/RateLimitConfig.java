package com.malugu.springboot_starter_project.config.graphql;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "app.rate-limit")
@Getter
@Setter
public class RateLimitConfig {
	private Global global = new Global();
	private Mutation mutation = new Mutation();
	private Ip ip = new Ip();
	private Depth depth = new Depth();
	private Introspection introspection = new Introspection();
	private Execution execution = new Execution();

	@Getter
	@Setter
	public static class Global {
		private boolean enabled;
		private long capacity;
		private long refillAmount;
		private Duration refillPeriod;
	}

	@Getter
	@Setter
	public static class Mutation {
		private boolean enabled;
		private long capacity;
		private long refillAmount;
		private Duration refillPeriod;
	}

	@Getter
	@Setter
	public static class Ip {
		private boolean enabled;
		private long capacity;
		private long refillAmount;
		private Duration refillPeriod;
	}

	@Getter
	@Setter
	public static class Depth {
		private int maxDepth;
		private int maxComplexity;
	}

	@Getter
	@Setter
	public static class Introspection {
		private boolean enabled;
	}

	@Getter
	@Setter
	public static class Execution {
		private Duration timeout;
	}
}

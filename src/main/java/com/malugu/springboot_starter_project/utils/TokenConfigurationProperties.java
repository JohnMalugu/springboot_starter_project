package com.malugu.springboot_starter_project.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "token")
@Getter
@Setter
public class TokenConfigurationProperties {
	private Signing signing = new Signing();
	private Lifespan lifespan = new Lifespan();

	@Getter
	@Setter
	public static class Signing {
		private String accessTokenSecret;
		private String refreshTokenSecret;
		private String passwordResetSecret;
	}

	@Getter
	@Setter
	public static class Lifespan {
		private Duration accessTokenLifespan;
		private Duration refreshTokenLifespan;
		private Duration passwordResetLifespan;
	}

}

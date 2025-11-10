package com.malugu.springboot_starter_project.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SpringSecurityAuditorAwareConfig {

	@Bean
	public AuditorAware<Long> auditorAware() {
		return new SpringSecurityAuditorAware();
	}
}

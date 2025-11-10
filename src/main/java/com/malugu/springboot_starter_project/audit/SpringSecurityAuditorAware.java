package com.malugu.springboot_starter_project.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<Long> {

	public Optional<Long> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.empty();
		}
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(authentication.getPrincipal()));
			System.err.println(" User Id: " + jsonNode.path("attributes").path("id") + " Created Or Updated something");
			if (!authentication.getName().contentEquals("anonymousUser")) {
				return Optional.ofNullable(Long.valueOf(jsonNode.path("attributes").path("id").asLong()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

}

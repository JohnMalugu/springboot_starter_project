package com.malugu.springboot_starter_project.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class AuditRevisionListener implements RevisionListener {

	@Override
	public void newRevision(Object revisionEntity) {
		CustomAuditRevisionEntity audit = (CustomAuditRevisionEntity) revisionEntity;
		String username = "SYSTEM.DEFAULT";
		String ipAddress = "127.0.0.1";
		Long userId = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.registerModule(new JavaTimeModule());
				objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
				JsonNode jsonNode = objectMapper
						.readTree(objectMapper.writeValueAsString(authentication.getPrincipal()));
				System.err.println(
						" User Id: " + jsonNode.path("attributes").path("id") + " Created Or Updated something");
				userId = jsonNode.path("attributes").path("id").asLong();
				username = authentication.getName();
				WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
				ipAddress = details.getRemoteAddress();
				System.err.println("==============================");
				System.err.println(ipAddress);
				System.err.println("==============================");
			} catch (Exception e) {
			}
		}
		audit.setUsername(username);
		audit.setUserId(userId);
		audit.setIpAddress(ipAddress);
	}

}

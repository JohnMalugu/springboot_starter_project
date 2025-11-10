package com.malugu.springboot_starter_project.uaa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import tz.go.ega.uaa.enums.TokenType;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "opaque_tokens")
public class OpaqueToken {

	@Id
	private String tokenValue;

	private String username;

	private String userIp;

	@Enumerated(EnumType.STRING) // Store enum as string in DB
	@Column(nullable = false)
	private TokenType tokenType; // Differentiates ACCESS from REFRESH

	@CreationTimestamp
	private LocalDateTime createdAt;

	private String scopes; // e.g., "read write"

	@Column(nullable = false)
	private Instant issuedAt;

	@Column(nullable = false)
	private Instant expiresAt;

	private boolean active = true; // To support token revocation

	public OpaqueToken(String tokenValue, String username, String userIp, String scopes, Instant issuedAt,
			Instant expiresAt) {
		super();
		this.tokenValue = tokenValue;
		this.username = username;
		this.userIp = userIp;
		this.scopes = scopes;
		this.issuedAt = issuedAt;
		this.expiresAt = expiresAt;
	}


}

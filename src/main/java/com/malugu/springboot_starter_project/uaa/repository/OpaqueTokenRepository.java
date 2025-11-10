package com.malugu.springboot_starter_project.uaa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.malugu.springboot_starter_project.uaa.entity.OpaqueToken;
import com.malugu.springboot_starter_project.uaa.enums.TokenType;

import java.util.Optional;

public interface OpaqueTokenRepository extends JpaRepository<OpaqueToken, String> {
	Optional<OpaqueToken> findFirstByTokenValue(String tokenValue);

	Optional<OpaqueToken> findFirstByUsernameAndTokenTypeAndActiveTrue(String subject, TokenType refresh);

	Optional<OpaqueToken> findFirstByTokenValueAndTokenType(String token, TokenType access);
}

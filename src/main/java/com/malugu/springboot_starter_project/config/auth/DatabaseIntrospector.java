package com.malugu.springboot_starter_project.config.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import tz.go.ega.uaa.entity.OpaqueToken;
import tz.go.ega.uaa.entity.UserAccount;
import tz.go.ega.uaa.enums.TokenType;
import tz.go.ega.uaa.repository.UserAccountRepository;

import java.util.*;

@Component
public class DatabaseIntrospector implements OpaqueTokenIntrospector {

	private final UserAccountRepository userAccountRepository;

	private final TokenIssuerService tokenIssuerService;

	public DatabaseIntrospector(UserAccountRepository userAccountRepository,
			TokenIssuerService tokenIssuerService) {
		this.userAccountRepository = userAccountRepository;
		this.tokenIssuerService = tokenIssuerService;
    }

	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		Optional<OpaqueToken> tokenOptional = tokenIssuerService.validateAndGetActiveTokenDetails(token,
				TokenType.ACCESS);

		if (tokenOptional.isPresent()) {
			OpaqueToken opaqueToken = tokenOptional.get();

			Optional<UserAccount> userAccount = userAccountRepository.findFirstByUsername(opaqueToken.getUsername());
			Map<String, Object> attributes = new HashMap<>();
			attributes.put("details", opaqueToken.getUsername());
			List<GrantedAuthority> authorities = new ArrayList<>();
			// 1. Split the string by comma
			String[] scopesArray = opaqueToken.getScopes() != null ? opaqueToken.getScopes().split(",")
					: "read,".split(",");

			// 2. Convert the array to a List<String>
			List<String> scopesList = Arrays.asList(scopesArray);
			attributes.put("scopes", scopesList);
			if (userAccount.isPresent()) {
				userAccount.get().getAuthorities().stream().forEach(pm -> {
					authorities.add(pm);
				});
				attributes.put("details", userAccount.get());
				attributes.put("sub", opaqueToken.getUsername());
				attributes.put("username", opaqueToken.getUsername());
				attributes.put("id", userAccount.get().getId());
				attributes.put("uuid", userAccount.get().getUuid());
				attributes.put("iat", opaqueToken.getIssuedAt());
				attributes.put("exp", opaqueToken.getExpiresAt());
				attributes.put("active", opaqueToken.isActive());
			}

//			attributes.put("username", opaqueToken.getUsername());
			attributes.put("scopes", opaqueToken.getScopes());

//			return new DefaultOAuth2AuthenticatedPrincipal(opaqueToken.getUsername(), attributes, authorities);
			return new OAuth2AuthenticatedPrincipal() {
				@Override
				public String getName() {
					return opaqueToken.getUsername();
				}

				@Override
				public Map<String, Object> getAttributes() {
					return attributes;
				}

				@Override
				public Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
					return authorities;
				}
			};
		} else {
			throw new BadOpaqueTokenException("Invalid token");
		}
	}

}
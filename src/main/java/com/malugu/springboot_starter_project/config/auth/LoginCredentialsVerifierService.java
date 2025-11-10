package com.malugu.springboot_starter_project.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LoginCredentialsVerifierService {

	@Autowired
	private AuthenticationManager authenticationManager;

	public boolean verify(String username, String password, String clientIpAddress) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		return authentication != null ? authentication.isAuthenticated() : false;
	}

}

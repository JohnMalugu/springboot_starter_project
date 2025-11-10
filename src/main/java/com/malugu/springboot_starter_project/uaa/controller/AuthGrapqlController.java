package com.malugu.springboot_starter_project.uaa.controller;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tz.go.ega.uaa.entity.UserAccount;
import tz.go.ega.uaa.repository.UserAccountRepository;

import java.util.Optional;

@Service
@GraphQLApi
public class AuthGrapqlController {

	@Autowired
	private UserAccountRepository userAccountRepository;

	@GraphQLQuery(name = "myDetails", description = "Get Current Authenticated user details")
	public UserAccount getMyDetail() {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String username = "";
			if (auth != null) {
				System.err.println("User Detail: " + auth.getName());
				username = auth.getName();
			}
			Optional<UserAccount> userAccount = userAccountRepository.findFirstByUsername(username);
			if (userAccount.isPresent()) {
				return userAccount.get();
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

package com.malugu.springboot_starter_project.uaa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tz.go.ega.uaa.dto.UserAccountDto;
import tz.go.ega.uaa.entity.UserAccount;
import tz.go.ega.uaa.serviceImpl.UserAccountServiceImpl;

@RestController
public class UserController {

	@Autowired
	private UserAccountServiceImpl userAccountService;

	@PostMapping(value = "/user/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserAccount> saveUser(@RequestBody UserAccountDto userAccountDto) {
		return ResponseEntity
				.ok(userAccountService.createUserAccount(userAccountDto.getEmail(), userAccountDto.getPassword()));
	}
}

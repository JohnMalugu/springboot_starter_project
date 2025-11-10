package com.malugu.springboot_starter_project.uaa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {

	private String username;
	private String password;

	// scopes separated by comma
	private String scopes;

}

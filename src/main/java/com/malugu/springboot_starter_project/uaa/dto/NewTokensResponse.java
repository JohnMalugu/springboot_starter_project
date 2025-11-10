package com.malugu.springboot_starter_project.uaa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewTokensResponse {
	private String accessToken;
	private String refreshToken;
	private long accessTokenExpiresIn;
	private long refreshTokenExpiresIn;
}

package com.malugu.springboot_starter_project.uaa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
	private String refreshToken;
	private String accessToken;
}

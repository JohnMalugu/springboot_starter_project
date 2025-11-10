package com.malugu.springboot_starter_project.uaa.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.malugu.springboot_starter_project.config.auth.LoginCredentialsVerifierService;
import com.malugu.springboot_starter_project.config.auth.TokenIssuerService;
import com.malugu.springboot_starter_project.uaa.dto.*;
import com.malugu.springboot_starter_project.uaa.entity.UserAccount;
import com.malugu.springboot_starter_project.uaa.enums.TokenType;
import com.malugu.springboot_starter_project.uaa.repository.UserAccountRepository;
import com.malugu.springboot_starter_project.uaa.serviceImpl.UserAccountServiceImpl;
import com.malugu.springboot_starter_project.utils.RequestIpUtil;

import java.security.Principal;
import java.util.Optional;

@RestController
public class AuthController {

	@Autowired
	private LoginCredentialsVerifierService credentialsVerifierService;

	@Autowired
	private TokenIssuerService tokenService;

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Autowired
	private UserAccountServiceImpl userService;

	@PostMapping(value = "/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getToken(@RequestBody UserLoginDto loginDto, HttpServletRequest request) {
		try {
			String clientIpAddress = RequestIpUtil.getClientIpAddress(request);
			boolean verified = credentialsVerifierService.verify(loginDto.getUsername(), loginDto.getPassword(),
					clientIpAddress);
			if (verified) {
				return tokenService.createReturnedToken(loginDto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
	}

	@PostMapping(value = "/auth/refresh-token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshRequest) {
		try {
			Optional<NewTokensResponse> response = tokenService.refreshAccessToken(refreshRequest.getRefreshToken(),
					1440,
					7);
			if (response.isPresent()) {
				return ResponseEntity.status(HttpStatus.OK).body(response.get());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
	}

	@GetMapping(value = "/auth/user-info", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> introspectToken(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
		try {
			return ResponseEntity.ok(principal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User details was not found");
	}

	@PostMapping(value = "/auth/logout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> logout(@RequestBody LogoutRequest logoutRequest) {
		// Invalidate the refresh token. This effectively logs out the user's session.
		if (logoutRequest != null && logoutRequest.getRefreshToken() != null) {
			tokenService.revokeToken(logoutRequest.getRefreshToken(), TokenType.REFRESH);
		}
		if (logoutRequest != null && logoutRequest.getAccessToken() != null) {
			tokenService.revokeToken(logoutRequest.getAccessToken(), TokenType.ACCESS);
		}
		// For access token, it's typically short-lived, so immediate revocation is less
		// critical
		// unless you're implementing a blacklisting mechanism.
		// tokenService.revokeAccessToken(logoutRequest.getAccessToken()); // Optional:
		// if you passed accessToken too

		return ResponseEntity.ok("Logged out successfully.");
	}

	// --- Password Reset Functionality ---

	@PostMapping("/auth/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
		// In a real app, always return a generic success message to prevent username
		// enumeration.
		// Also, add rate limiting.
		Optional<String> resetToken = tokenService.generatePasswordResetToken(request.getUsername());

		if (resetToken.isPresent()) {
			// In a real app: Send this token via email to the user.
			// For demo: Print to console
			System.out.println("Password Reset Link for " + request.getUsername() + ":");
			System.out.println("http://localhost:8080/reset-password?token=" + resetToken.get());
		}

		// Always return 200 OK to prevent username enumeration, even if user not found.
		return ResponseEntity.ok("If an account with that username exists, a password reset link has been sent.");
	}

	@PostMapping("/auth/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
		if (tokenService.resetPassword(request.getToken(), request.getNewPassword())) {
			return ResponseEntity.ok("Password has been reset successfully. Please log in with your new password.");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired password reset token.");
		}
	}

	// --- Password Update (for authenticated users) ---

	@PostMapping("/auth/update-password")
	public ResponseEntity<String> updatePassword(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
			@RequestBody UpdatePasswordRequest request) {
		String username = principal.getName(); // Get username from authenticated principal

		if (userService.changePassword(username, request.getOldPassword(), request.getNewPassword())) {
			return ResponseEntity.ok("Password updated successfully.");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Old password incorrect.");
		}
	}

	@PostMapping("/myDetails")
	@PreAuthorize("hasAnyRole('ROLE_USER')")
	public ResponseEntity<?> getMyDetail(Principal principal) {
		try {
			Optional<UserAccount> userAccount = userAccountRepository.findFirstByUsername(principal.getName());
			if (userAccount.isPresent()) {
				return ResponseEntity.ok(userAccount.get());
			}
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}

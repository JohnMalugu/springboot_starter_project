package com.malugu.springboot_starter_project.config.auth;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tz.go.ega.uaa.dto.NewTokensResponse;
import tz.go.ega.uaa.dto.UserLoginDto;
import tz.go.ega.uaa.entity.OpaqueToken;
import tz.go.ega.uaa.entity.UserAccount;
import tz.go.ega.uaa.enums.TokenType;
import tz.go.ega.uaa.repository.OpaqueTokenRepository;
import tz.go.ega.uaa.service.EmailService;
import tz.go.ega.uaa.serviceImpl.UserAccountServiceImpl;
import tz.go.ega.utils.TokenConfigurationProperties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenIssuerService {

	private final OpaqueTokenRepository opaqueTokenRepository;
	private final UserAccountServiceImpl userService;
	private final EmailService emailService; // Inject EmailService
	private final TokenConfigurationProperties tokenConfigurationProperties; // Inject EmailService

	@Value("${token.signing.access-token-secret}")
	private String hmacSecretAccess;

	@Value("${token.signing.refresh-token-secret}")
	private String hmacSecretRefresh;

	@Value("${token.signing.password-reset-secret}") // New secret for password reset tokens
	private String hmacSecretPasswordReset;

	// These need to be public static or exposed via getters if AuthController
	// accesses them directly
	// Or, better, AuthController asks TokenService to generate/verify the full
	// signed string.
	// For simplicity, we'll keep them private and have
	// generateSignedOpaqueToken/verifySignedOpaqueToken
	// handle key selection based on TokenType.
	private SecretKeySpec accessTokenSecretKeySpec;
	private SecretKeySpec refreshTokenSecretKeySpec;
	private SecretKeySpec passwordResetSecretKeySpec;

//	public TokenIssuerService(OpaqueTokenRepository opaqueTokenRepository, UserAccountServiceImpl userService,
//			EmailService emailService) {
//		this.opaqueTokenRepository = opaqueTokenRepository;
//		this.userService = userService;
//		this.emailService = emailService;
//	}

	@PostConstruct
	public void init() {
		try {
			// We have two options here please choose what you prefer

//			this.accessTokenSecretKeySpec = new SecretKeySpec(Base64.getUrlDecoder().decode(hmacSecretAccess),
//					"HmacSHA256");
//			this.refreshTokenSecretKeySpec = new SecretKeySpec(Base64.getUrlDecoder().decode(hmacSecretRefresh),
//					"HmacSHA256");
//			this.passwordResetSecretKeySpec = new SecretKeySpec(Base64.getUrlDecoder().decode(hmacSecretPasswordReset),
//					"HmacSHA256");
			this.accessTokenSecretKeySpec = new SecretKeySpec(
					Base64.getUrlDecoder().decode(tokenConfigurationProperties.getSigning().getAccessTokenSecret()),
					"HmacSHA256");
			this.refreshTokenSecretKeySpec = new SecretKeySpec(
					Base64.getUrlDecoder().decode(tokenConfigurationProperties.getSigning().getRefreshTokenSecret()),
					"HmacSHA256");
			this.passwordResetSecretKeySpec = new SecretKeySpec(
					Base64.getUrlDecoder().decode(tokenConfigurationProperties.getSigning().getPasswordResetSecret()),
					"HmacSHA256");
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("HMAC secret keys are not valid Base64 encoded strings.", e);
		}
	}

	// --- HMAC Signing/Verification Helper Methods ---

	private SecretKeySpec getSecretKeySpecForType(TokenType type) {
		return switch (type) {
		case ACCESS -> accessTokenSecretKeySpec;
		case REFRESH -> refreshTokenSecretKeySpec;
		case PASSWORD_RESET -> passwordResetSecretKeySpec;
		};
	}

	/**
	 * Generates an HMAC-SHA256 signature for a base token value and combines it.
	 * Format: baseTokenValue.HMAC_SIGNATURE
	 */
	public String generateSignedOpaqueToken(String baseTokenValue, TokenType tokenType) {
		SecretKeySpec secretKeySpec = getSecretKeySpecForType(tokenType);
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(secretKeySpec);
			byte[] hmacBytes = mac.doFinal(baseTokenValue.getBytes());
			String hmac = Base64.getUrlEncoder().encodeToString(hmacBytes);
			return baseTokenValue + "." + hmac;
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new IllegalStateException("Error signing token", e);
		}
	}

	/**
	 * Verifies the HMAC signature of a signed token. Returns the base token value
	 * if the signature is valid, otherwise Optional.empty().
	 */
	public Optional<String> verifySignedOpaqueToken(String signedToken, TokenType tokenType) {
		SecretKeySpec secretKeySpec = getSecretKeySpecForType(tokenType);
		if (!signedToken.contains(".")) {
			return Optional.empty(); // Not a signed token format
		}
		String[] parts = signedToken.split("\\.", 2);
		String baseTokenValue = parts[0];
		String receivedHmac = parts[1];

		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(secretKeySpec);
			byte[] expectedHmacBytes = mac.doFinal(baseTokenValue.getBytes());
			String expectedHmac = Base64.getUrlEncoder().encodeToString(expectedHmacBytes);

			if (expectedHmac.equals(receivedHmac)) {
				return Optional.of(baseTokenValue); // Signature valid, return original base value
			}
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			System.err.println("Error verifying token signature for " + tokenType + " token: " + e.getMessage());
		}
		return Optional.empty(); // Signature invalid
	}

	// --- Token Creation Methods (Consolidated) ---

	public OpaqueToken createAndStoreToken(String username, TokenType type, String scopes) {
//		public OpaqueToken createAndStoreToken(String username, TokenType type, String scopes, long lifespanDuration,
//				ChronoUnit lifespanUnit) {
		// Invalidate any existing active refresh token for this username if it's a new
		// refresh token
		if (type == TokenType.REFRESH) {
			opaqueTokenRepository.findFirstByUsernameAndTokenTypeAndActiveTrue(username, TokenType.REFRESH)
					.ifPresent(existingToken -> {
						existingToken.setActive(false);
						opaqueTokenRepository.save(existingToken);
					});
		}

		String baseTokenValue = UUID.randomUUID().toString(); // The UUID stored in DB
		Instant now = Instant.now();
		Instant expiresAt = now.plus(tokenConfigurationProperties.getLifespan().getAccessTokenLifespan());
		if (type == TokenType.REFRESH) {
			expiresAt = now.plus(tokenConfigurationProperties.getLifespan().getRefreshTokenLifespan());
		}
		if (type == TokenType.PASSWORD_RESET) {
			expiresAt = now.plus(tokenConfigurationProperties.getLifespan().getPasswordResetLifespan());
		}
//		Instant expiresAt = now.plus(lifespanDuration, lifespanUnit);

		OpaqueToken token = new OpaqueToken();
		token.setTokenValue(baseTokenValue);
		token.setUsername(username);
		token.setTokenType(type); // Set the type!
		token.setScopes(scopes); // Scopes may be null for REFRESH
		token.setIssuedAt(now);
		token.setExpiresAt(expiresAt);
		token.setActive(true);

		return opaqueTokenRepository.save(token);
	}

	// --- Token Validation Methods (Consolidated) ---

	public Optional<OpaqueToken> validateAndGetActiveTokenDetails(String signedToken, TokenType expectedType) {
		return verifySignedOpaqueToken(signedToken, expectedType).flatMap(opaqueTokenRepository::findFirstByTokenValue)
				.filter(OpaqueToken::isActive).filter(token -> token.getTokenType() == expectedType) // Ensure correct
																										// type
				.filter(token -> token.getExpiresAt().isAfter(Instant.now()));
	}

	// --- Token Revocation Methods (Consolidated) ---

	public void revokeToken(String signedToken, TokenType type) {
		verifySignedOpaqueToken(signedToken, type).flatMap(opaqueTokenRepository::findFirstByTokenValue)
				.ifPresent(token -> {
			token.setActive(false);
			opaqueTokenRepository.save(token);
		});
	}

	public ResponseEntity<Object> createReturnedToken(UserLoginDto loginDto) {
		try {
			// Issue a short-lived access token
			OpaqueToken accessTokenDb = this.createAndStoreToken(loginDto.getUsername(), TokenType.ACCESS,
					loginDto.getScopes());
			// Sign the access token for the client
			String signedAccessToken = this.generateSignedOpaqueToken(accessTokenDb.getTokenValue(), TokenType.ACCESS);

			// Issue a long-lived refresh token
			OpaqueToken refreshTokenDb = this.createAndStoreToken(loginDto.getUsername(), TokenType.REFRESH,
					loginDto.getScopes());
			// Refresh
			// scopes
			// Refresh token valid for 7 days
			// Sign the refresh token for the client
			String signedRefreshToken = this.generateSignedOpaqueToken(refreshTokenDb.getTokenValue(),
					TokenType.REFRESH);

			return ResponseEntity.ok(new NewTokensResponse(signedAccessToken, signedRefreshToken,
					accessTokenDb.getExpiresAt().getEpochSecond() - accessTokenDb.getIssuedAt().getEpochSecond(),
					refreshTokenDb.getExpiresAt().getEpochSecond() - refreshTokenDb.getIssuedAt().getEpochSecond()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
	}


	/**
	 * Attempts to refresh an access token using a valid refresh token. On
	 * successful refresh, the old refresh token is typically invalidated and a new
	 * one is issued (rotation).
	 *
	 * @param signedRefreshToken         The refresh token provided by the client
	 *                                   (with HMAC).
	 * @param accessTokenLifespanMinutes The desired lifespan for the new access
	 *                                   token.
	 * @param refreshTokenLifespanDays   The desired lifespan for the new refresh
	 *                                   token.
	 * @return An Optional containing a {@link NewTokensResponse} if successful.
	 */
	public Optional<NewTokensResponse> refreshAccessToken(String signedRefreshToken, long accessTokenLifespanMinutes,
			long refreshTokenLifespanDays) {
		return validateAndGetActiveTokenDetails(signedRefreshToken, TokenType.REFRESH).map(refreshToken -> {
			// 1. Invalidate the used refresh token (Important for refresh token rotation)
			refreshToken.setActive(false);
			opaqueTokenRepository.save(refreshToken);

			// 2. Generate a new access token (stores base UUID)
			OpaqueToken newAccessTokenDb = createAndStoreToken(refreshToken.getUsername(), TokenType.ACCESS,
					refreshToken.getScopes());
			// Sign the new access token value for client
			String newAccessTokenClient = generateSignedOpaqueToken(newAccessTokenDb.getTokenValue(), TokenType.ACCESS);


			// 3. Generate a new refresh token (stores base UUID)
			OpaqueToken newRefreshTokenDb = createAndStoreToken(refreshToken.getUsername(), TokenType.REFRESH,
					null);
			// Sign the new refresh token value for client
			String newRefreshTokenClient = generateSignedOpaqueToken(newRefreshTokenDb.getTokenValue(),
					TokenType.REFRESH);

			return new NewTokensResponse(newAccessTokenClient, newRefreshTokenClient,
					newAccessTokenDb.getExpiresAt().getEpochSecond() - newAccessTokenDb.getIssuedAt().getEpochSecond(),
					newRefreshTokenDb.getExpiresAt().getEpochSecond()
							- newRefreshTokenDb.getIssuedAt().getEpochSecond());
		});
	}


	// --- Password Reset Specific Methods ---

	/**
	 * Generates a password reset token for a given username and sends an email.
	 *
	 * @param username The username for which to generate a reset token.
	 * @param toEmail  The email address to send the reset link to.
	 * @return The signed password reset token string, or Optional.empty() if user
	 *         not found.
	 */
	public Optional<String> generatePasswordResetToken(String username) {
		return userService.findByUsername(username).map(user -> {
			OpaqueToken resetTokenDb = createAndStoreToken(user.getUsername(), TokenType.PASSWORD_RESET, null);
			// Sign the reset token for the client
			String signedResetToken = generateSignedOpaqueToken(resetTokenDb.getTokenValue(), TokenType.PASSWORD_RESET);

			try {
				emailService.sendPasswordResetEmail(username, user.getUsername(), signedResetToken);
				System.out.println("Password reset email sent to " + username + " for user " + user.getUsername());
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Failed to send password reset email to " + username + ": " + e.getMessage());
				// In a real application, you might want to log this error more robustly
				// and potentially revert the token creation or mark it as unsent.
			}

			return signedResetToken; // Return the token even if email sending failed, for logging/debugging
		});
	}

	/**
	 * Resets a user's password using a valid password reset token. This operation
	 * should be transactional.
	 *
	 * @param signedResetToken The signed password reset token from the user.
	 * @param newPassword      The new password to set for the user.
	 * @return true if password was successfully reset, false otherwise.
	 */
//	@Transactional // Ensure atomicity
	public boolean resetPassword(String signedResetToken, String newPassword) {
		Optional<OpaqueToken> tokenOptional = validateAndGetActiveTokenDetails(signedResetToken,
				TokenType.PASSWORD_RESET);

		if (tokenOptional.isPresent()) {
			OpaqueToken resetToken = tokenOptional.get();
			Optional<UserAccount> userOptional = userService.findByUsername(resetToken.getUsername());

			if (userOptional.isPresent()) {
				userService.updatePassword(userOptional.get(), newPassword);
				// Invalidate the reset token immediately after use
				resetToken.setActive(false);
				opaqueTokenRepository.save(resetToken);
				return true;
			}
		}
		return false;
	}
}
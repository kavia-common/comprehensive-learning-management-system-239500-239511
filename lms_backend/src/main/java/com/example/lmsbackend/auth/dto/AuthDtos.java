package com.example.lmsbackend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class AuthDtos {

	public record RegisterRequest(
			@Schema(example = "student@example.com") @NotBlank @Email String email,
			@Schema(example = "StrongPassw0rd!") @NotBlank @Size(min = 8, max = 100) String password,
			@Schema(example = "Student One") @NotBlank @Size(min = 2, max = 100) String displayName
	) {
	}

	public record LoginRequest(
			@NotBlank @Email String email,
			@NotBlank String password
	) {
	}

	public record TokenResponse(
			String accessToken,
			String refreshToken,
			List<String> roles
	) {
	}

	public record RefreshRequest(
			@NotBlank String refreshToken
	) {
	}

	public record LogoutRequest(
			@NotBlank String refreshToken
	) {
	}
}

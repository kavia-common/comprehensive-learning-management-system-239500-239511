package com.example.lmsbackend.auth;

import com.example.lmsbackend.auth.dto.AuthDtos;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	@Operation(summary = "Register a new user", description = "Registers a user with default STUDENT role and returns access/refresh tokens.")
	public ResponseEntity<AuthDtos.TokenResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest req) {
		return ResponseEntity.ok(authService.register(req));
	}

	@PostMapping("/login")
	@Operation(summary = "Login", description = "Authenticates credentials and returns access/refresh tokens.")
	public ResponseEntity<AuthDtos.TokenResponse> login(@Valid @RequestBody AuthDtos.LoginRequest req) {
		return ResponseEntity.ok(authService.login(req));
	}

	@PostMapping("/refresh")
	@Operation(summary = "Refresh token", description = "Rotates refresh token and returns a new access/refresh token pair.")
	public ResponseEntity<AuthDtos.TokenResponse> refresh(@Valid @RequestBody AuthDtos.RefreshRequest req) {
		return ResponseEntity.ok(authService.refresh(req));
	}

	@PostMapping("/logout")
	@Operation(summary = "Logout", description = "Revokes the provided refresh token.")
	public ResponseEntity<Void> logout(@Valid @RequestBody AuthDtos.LogoutRequest req) {
		authService.logout(req);
		return ResponseEntity.noContent().build();
	}
}

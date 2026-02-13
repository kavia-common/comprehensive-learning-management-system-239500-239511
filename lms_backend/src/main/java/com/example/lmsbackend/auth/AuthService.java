package com.example.lmsbackend.auth;

import com.example.lmsbackend.auth.dto.AuthDtos;
import com.example.lmsbackend.security.JwtTokenService;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserRoleRepository userRoleRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenService jwtTokenService;

	public AuthService(UserRepository userRepository,
	                   RoleRepository roleRepository,
	                   UserRoleRepository userRoleRepository,
	                   RefreshTokenRepository refreshTokenRepository,
	                   PasswordEncoder passwordEncoder,
	                   JwtTokenService jwtTokenService) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userRoleRepository = userRoleRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenService = jwtTokenService;
	}

	@Transactional
	public AuthDtos.TokenResponse register(AuthDtos.RegisterRequest req) {
		if (userRepository.findByEmail(req.email()).isPresent()) {
			throw new IllegalArgumentException("Email already registered");
		}

		User user = new User(req.email(), passwordEncoder.encode(req.password()), req.displayName());
		user = userRepository.save(user);

		Role student = roleRepository.findByName(Role.Name.STUDENT).orElseThrow();
		userRoleRepository.addRoleToUser(user.getId(), student.getId());

		List<String> roles = List.of(Role.Name.STUDENT.name());
		String access = jwtTokenService.createAccessToken(user.getId(), user.getEmail(), roles);
		String refresh = jwtTokenService.createRefreshToken(user.getId());

		saveRefreshToken(user.getId(), refresh);

		return new AuthDtos.TokenResponse(access, refresh, roles);
	}

	public AuthDtos.TokenResponse login(AuthDtos.LoginRequest req) {
		User user = userRepository.findByEmail(req.email()).orElseThrow(() -> new EntityNotFoundException("User not found"));
		if (!user.isEnabled()) throw new IllegalArgumentException("User disabled");
		if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
			throw new IllegalArgumentException("Invalid credentials");
		}

		List<String> roles = userRoleRepository.findRoleNamesByUserId(user.getId());
		String access = jwtTokenService.createAccessToken(user.getId(), user.getEmail(), roles);
		String refresh = jwtTokenService.createRefreshToken(user.getId());

		saveRefreshToken(user.getId(), refresh);

		return new AuthDtos.TokenResponse(access, refresh, roles);
	}

	@Transactional
	public AuthDtos.TokenResponse refresh(AuthDtos.RefreshRequest req) {
		Claims claims = jwtTokenService.parse(req.refreshToken());
		if (!jwtTokenService.isRefreshToken(claims)) {
			throw new IllegalArgumentException("Not a refresh token");
		}

		String hash = sha256(req.refreshToken());
		RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
				.orElseThrow(() -> new IllegalArgumentException("Unknown refresh token"));

		if (stored.isRevoked()) throw new IllegalArgumentException("Refresh token revoked");
		if (stored.getExpiresAt().isBefore(Instant.now())) throw new IllegalArgumentException("Refresh token expired");

		long userId = Long.parseLong(claims.getSubject());
		User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
		List<String> roles = userRoleRepository.findRoleNamesByUserId(userId);

		String newAccess = jwtTokenService.createAccessToken(userId, user.getEmail(), roles);

		stored.revoke();
		refreshTokenRepository.save(stored);

		String newRefresh = jwtTokenService.createRefreshToken(userId);
		saveRefreshToken(userId, newRefresh);

		return new AuthDtos.TokenResponse(newAccess, newRefresh, roles);
	}

	@Transactional
	public void logout(AuthDtos.LogoutRequest req) {
		String hash = sha256(req.refreshToken());
		refreshTokenRepository.findByTokenHash(hash).ifPresent(rt -> {
			rt.revoke();
			refreshTokenRepository.save(rt);
		});
	}

	private void saveRefreshToken(long userId, String refreshToken) {
		Claims claims = jwtTokenService.parse(refreshToken);
		Instant exp = jwtTokenService.getExpiration(claims);
		refreshTokenRepository.save(new RefreshToken(userId, sha256(refreshToken), exp));
	}

	private static String sha256(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(digest);
		} catch (Exception e) {
			throw new IllegalStateException("Cannot hash token", e);
		}
	}
}

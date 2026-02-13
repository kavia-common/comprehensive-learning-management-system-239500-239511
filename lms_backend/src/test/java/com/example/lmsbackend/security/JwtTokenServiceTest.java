package com.example.lmsbackend.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenServiceTest {

	@Test
	void creates_and_parses_access_token() {
		JwtProperties props = new JwtProperties();
		props.setSecret("unit-test-secret-unit-test-secret-unit-test-secret");
		props.setAccessTtlSeconds(60);
		props.setRefreshTtlSeconds(600);

		JwtTokenService svc = new JwtTokenService(props);
		String jwt = svc.createAccessToken(123L, "u@example.com", List.of("STUDENT"));

		Claims claims = svc.parse(jwt);
		assertThat(claims.getSubject()).isEqualTo("123");
		assertThat(claims.get("email", String.class)).isEqualTo("u@example.com");
		assertThat(svc.getRoles(claims)).contains("STUDENT");
	}
}

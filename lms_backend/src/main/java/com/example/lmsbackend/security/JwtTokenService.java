package com.example.lmsbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenService {

	private final JwtProperties props;
	private final SecretKey key;

	public JwtTokenService(JwtProperties props) {
		this.props = props;
		this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
	}

	public String createAccessToken(long userId, String email, List<String> roles) {
		Instant now = Instant.now();
		Instant exp = now.plusSeconds(props.getAccessTtlSeconds());
		return Jwts.builder()
				.subject(String.valueOf(userId))
				.claim("email", email)
				.claim("roles", roles)
				.issuedAt(Date.from(now))
				.expiration(Date.from(exp))
				.signWith(key)
				.compact();
	}

	public String createRefreshToken(long userId) {
		Instant now = Instant.now();
		Instant exp = now.plusSeconds(props.getRefreshTtlSeconds());
		return Jwts.builder()
				.subject(String.valueOf(userId))
				.claim("typ", "refresh")
				.issuedAt(Date.from(now))
				.expiration(Date.from(exp))
				.signWith(key)
				.compact();
	}

	public Claims parse(String jwt) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(jwt)
				.getPayload();
	}

	public boolean isRefreshToken(Claims claims) {
		return "refresh".equals(claims.get("typ", String.class));
	}

	public Instant getExpiration(Claims claims) {
		return claims.getExpiration().toInstant();
	}

	@SuppressWarnings("unchecked")
	public List<String> getRoles(Claims claims) {
		Object roles = claims.get("roles");
		if (roles == null) return List.of();
		if (roles instanceof List<?> list) {
			return list.stream().map(String::valueOf).toList();
		}
		if (roles instanceof String s) return List.of(s);
		return List.of();
	}
}

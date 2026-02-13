package com.example.lmsbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtTokenService jwtTokenService;

	public JwtAuthFilter(JwtTokenService jwtTokenService) {
		this.jwtTokenService = jwtTokenService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (auth != null && auth.startsWith("Bearer ")) {
			String token = auth.substring("Bearer ".length()).trim();
			try {
				Claims claims = jwtTokenService.parse(token);
				if (!jwtTokenService.isRefreshToken(claims)) {
					long userId = Long.parseLong(claims.getSubject());
					String email = claims.get("email", String.class);
					List<SimpleGrantedAuthority> authorities = jwtTokenService.getRoles(claims).stream()
							.map(r -> new SimpleGrantedAuthority("ROLE_" + r))
							.toList();

					UsernamePasswordAuthenticationToken authentication =
							new UsernamePasswordAuthenticationToken(new Principal(userId, email), null, authorities);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			} catch (JwtException | IllegalArgumentException ignored) {
			}
		}

		filterChain.doFilter(request, response);
	}

	public record Principal(long userId, String email) {
	}
}

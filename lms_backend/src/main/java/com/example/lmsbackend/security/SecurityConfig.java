package com.example.lmsbackend.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http,
	                                      JwtTokenService jwtTokenService) throws Exception {

		http
				.csrf(csrf -> csrf.disable())
				.cors(Customizer.withDefaults())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/health", "/docs", "/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
						.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/ws/**").permitAll()
						.requestMatchers("/api/notifications/stream").authenticated()
						.anyRequest().authenticated()
				)
				.addFilterBefore(new JwtAuthFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}

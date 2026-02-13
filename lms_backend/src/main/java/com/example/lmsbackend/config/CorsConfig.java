package com.example.lmsbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	private final String allowedOrigins;

	public CorsConfig(@Value("${lms.cors.allowed-origins:*}") String allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		String[] origins = Arrays.stream(allowedOrigins.split(","))
				.map(String::trim)
				.filter(s -> !s.isBlank())
				.toArray(String[]::new);

		registry.addMapping("/**")
				.allowedOriginPatterns(origins.length == 0 ? new String[]{"*"} : origins)
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true)
				.maxAge(3600);
	}
}

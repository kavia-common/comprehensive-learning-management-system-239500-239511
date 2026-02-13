package com.example.lmsbackend.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lms.jwt")
public class JwtProperties {
	private String secret;
	private long accessTtlSeconds = 900;
	private long refreshTtlSeconds = 1209600;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public long getAccessTtlSeconds() {
		return accessTtlSeconds;
	}

	public void setAccessTtlSeconds(long accessTtlSeconds) {
		this.accessTtlSeconds = accessTtlSeconds;
	}

	public long getRefreshTtlSeconds() {
		return refreshTtlSeconds;
	}

	public void setRefreshTtlSeconds(long refreshTtlSeconds) {
		this.refreshTtlSeconds = refreshTtlSeconds;
	}
}

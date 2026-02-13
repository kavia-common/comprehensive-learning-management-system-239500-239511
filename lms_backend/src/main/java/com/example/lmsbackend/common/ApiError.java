package com.example.lmsbackend.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Standard API error payload")
public record ApiError(
		@Schema(description = "UTC timestamp") Instant timestamp,
		@Schema(description = "HTTP status code") int status,
		@Schema(description = "Error code") String code,
		@Schema(description = "Human readable message") String message,
		@Schema(description = "Optional field errors") Map<String, String> fieldErrors
) {
}

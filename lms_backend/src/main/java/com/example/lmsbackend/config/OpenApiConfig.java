package com.example.lmsbackend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
		info = @Info(
				title = "LMS Backend API",
				version = "0.1.0",
				description = "Spring Boot backend for a Learning Management System (LMS). Uses JWT authentication with RBAC."
		),
		tags = {
				@Tag(name = "Auth", description = "Authentication & token management"),
				@Tag(name = "Courses", description = "Course lifecycle and enrollment"),
				@Tag(name = "Assignments", description = "Assignments, submissions, grading"),
				@Tag(name = "Discussion", description = "Threads and posts"),
				@Tag(name = "Notifications", description = "WebSocket/SSE notification APIs"),
				@Tag(name = "Admin", description = "Administrative endpoints")
		}
)
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer"
)
public class OpenApiConfig {
}

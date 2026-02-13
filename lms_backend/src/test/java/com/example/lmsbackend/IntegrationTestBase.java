package com.example.lmsbackend;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class IntegrationTestBase {

	static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
			.withDatabaseName("lms")
			.withUsername("lms")
			.withPassword("lms");

	@BeforeAll
	static void start() {
		// CI environments often don't provide Docker; make integration tests opt-in.
		// To enable: set RUN_DOCKER_TESTS=true
		org.junit.jupiter.api.Assumptions.assumeTrue(
				"true".equalsIgnoreCase(System.getenv("RUN_DOCKER_TESTS")),
				"Docker-based integration tests are disabled (set RUN_DOCKER_TESTS=true to enable)."
		);

		postgres.start();
	}

	@DynamicPropertySource
	static void registerPg(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);

		registry.add("lms.jwt.secret", () -> "test-secret-test-secret-test-secret-test-secret");
		registry.add("lms.jwt.access-ttl-seconds", () -> "60");
		registry.add("lms.jwt.refresh-ttl-seconds", () -> "600");
	}
}

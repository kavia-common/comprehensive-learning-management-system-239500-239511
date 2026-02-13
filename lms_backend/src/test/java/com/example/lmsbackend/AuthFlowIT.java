package com.example.lmsbackend;

import com.example.lmsbackend.auth.dto.AuthDtos;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIT extends IntegrationTestBase {

	@Autowired MockMvc mvc;
	@Autowired ObjectMapper om;

	@Test
	void register_login_refresh_logout() throws Exception {
		AuthDtos.RegisterRequest register = new AuthDtos.RegisterRequest("a@example.com", "StrongPassw0rd!", "Alice");

		String regJson = mvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(register)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		AuthDtos.TokenResponse regTokens = om.readValue(regJson, AuthDtos.TokenResponse.class);
		assertThat(regTokens.accessToken()).isNotBlank();
		assertThat(regTokens.refreshToken()).isNotBlank();

		AuthDtos.LoginRequest login = new AuthDtos.LoginRequest("a@example.com", "StrongPassw0rd!");
		String loginJson = mvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(login)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		AuthDtos.TokenResponse loginTokens = om.readValue(loginJson, AuthDtos.TokenResponse.class);
		assertThat(loginTokens.accessToken()).isNotBlank();
		assertThat(loginTokens.refreshToken()).isNotBlank();

		AuthDtos.RefreshRequest refresh = new AuthDtos.RefreshRequest(loginTokens.refreshToken());
		String refreshJson = mvc.perform(post("/api/auth/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(refresh)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		AuthDtos.TokenResponse refreshed = om.readValue(refreshJson, AuthDtos.TokenResponse.class);
		assertThat(refreshed.accessToken()).isNotBlank();
		assertThat(refreshed.refreshToken()).isNotBlank();

		AuthDtos.LogoutRequest logout = new AuthDtos.LogoutRequest(refreshed.refreshToken());
		mvc.perform(post("/api/auth/logout")
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(logout)))
				.andExpect(status().isNoContent());
	}
}

package com.example.lmsbackend.notify;

import com.example.lmsbackend.security.JwtAuthFilter;
import com.example.lmsbackend.security.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

	private final NotificationService notificationService;
	private final JwtTokenService jwtTokenService;

	public NotificationController(NotificationService notificationService, JwtTokenService jwtTokenService) {
		this.notificationService = notificationService;
		this.jwtTokenService = jwtTokenService;
	}

	@GetMapping
	@Operation(summary = "Recent notifications", description = "Returns the most recent notifications for the authenticated user.")
	public List<Notification> recent(Authentication authentication) {
		long userId = ((JwtAuthFilter.Principal) authentication.getPrincipal()).userId();
		return notificationService.recent(userId);
	}

	@GetMapping("/stream")
	@Operation(
			summary = "SSE notification stream",
			description = "Server-Sent Events stream for real-time notifications. "
					+ "For browser EventSource clients, you may pass an `access_token` query param because EventSource cannot send an Authorization header."
	)
	public SseEmitter stream(Authentication authentication, HttpServletRequest request) {
		// Standard path: authenticated via Authorization: Bearer ...
		if (authentication != null && authentication.getPrincipal() instanceof JwtAuthFilter.Principal p) {
			return notificationService.subscribe(p.userId());
		}

		// EventSource fallback: authenticate via ?access_token=...
		String accessToken = request.getParameter("access_token");
		if (accessToken == null || accessToken.isBlank()) {
			throw new org.springframework.security.access.AccessDeniedException("Missing access token");
		}

		Claims claims = jwtTokenService.parse(accessToken);
		if (jwtTokenService.isRefreshToken(claims)) {
			throw new org.springframework.security.access.AccessDeniedException("Refresh token not allowed");
		}

		long userId = Long.parseLong(claims.getSubject());
		return notificationService.subscribe(userId);
	}
}

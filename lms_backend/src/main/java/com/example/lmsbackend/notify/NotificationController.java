package com.example.lmsbackend.notify;

import com.example.lmsbackend.security.JwtAuthFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

	public NotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@GetMapping
	@Operation(summary = "Recent notifications", description = "Returns the most recent notifications for the authenticated user.")
	public List<Notification> recent(Authentication authentication) {
		long userId = ((JwtAuthFilter.Principal) authentication.getPrincipal()).userId();
		return notificationService.recent(userId);
	}

	@GetMapping("/stream")
	@Operation(summary = "SSE notification stream", description = "Server-Sent Events stream for real-time notifications.")
	public SseEmitter stream(Authentication authentication) {
		long userId = ((JwtAuthFilter.Principal) authentication.getPrincipal()).userId();
		return notificationService.subscribe(userId);
	}
}

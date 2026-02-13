package com.example.lmsbackend.notify;

import com.example.lmsbackend.lms.Enrollment;
import com.example.lmsbackend.lms.repo.EnrollmentRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final EnrollmentRepository enrollmentRepository;
	private final SimpMessagingTemplate messagingTemplate;

	private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

	public NotificationService(NotificationRepository notificationRepository,
	                           EnrollmentRepository enrollmentRepository,
	                           SimpMessagingTemplate messagingTemplate) {
		this.notificationRepository = notificationRepository;
		this.enrollmentRepository = enrollmentRepository;
		this.messagingTemplate = messagingTemplate;
	}

	@Transactional
	public Notification notifyUser(long userId, String type, String message) {
		Notification n = notificationRepository.save(new Notification(userId, type, message));

		messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/notifications", toPayload(n));

		SseEmitter emitter = emitters.get(userId);
		if (emitter != null) {
			try {
				emitter.send(SseEmitter.event().name("notification").data(toPayload(n)));
			} catch (IOException e) {
				emitters.remove(userId);
			}
		}
		return n;
	}

	public void notifyCourseInstructors(long courseId, String type, String message) {
		enrollmentRepository.findByCourseId(courseId).stream()
				.filter(e -> e.getRoleInCourse() == Enrollment.RoleInCourse.INSTRUCTOR)
				.forEach(e -> notifyUser(e.getUserId(), type, message));
	}

	public void notifyCourseEnrolled(long courseId, String type, String message) {
		enrollmentRepository.findByCourseId(courseId).forEach(e -> notifyUser(e.getUserId(), type, message));
	}

	public List<Notification> recent(long userId) {
		return notificationRepository.findTop50ByUserIdOrderByCreatedAtDesc(userId);
	}

	public SseEmitter subscribe(long userId) {
		SseEmitter emitter = new SseEmitter(60_000L);
		emitters.put(userId, emitter);

		emitter.onCompletion(() -> emitters.remove(userId));
		emitter.onTimeout(() -> emitters.remove(userId));
		emitter.onError((ex) -> emitters.remove(userId));

		try {
			emitter.send(SseEmitter.event().name("connected").data(Map.of("userId", userId, "ts", Instant.now().toString())));
		} catch (IOException ignored) {
		}

		return emitter;
	}

	private static Map<String, Object> toPayload(Notification n) {
		return Map.of(
				"id", n.getId(),
				"userId", n.getUserId(),
				"type", n.getType(),
				"message", n.getMessage(),
				"createdAt", n.getCreatedAt().toString(),
				"readAt", n.getReadAt() == null ? null : n.getReadAt().toString()
		);
	}
}

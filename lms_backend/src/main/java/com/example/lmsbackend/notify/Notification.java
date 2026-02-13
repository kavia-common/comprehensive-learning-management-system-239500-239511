package com.example.lmsbackend.notify;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "notifications")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(nullable = false, length = 50)
	private String type;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String message;

	@Column(name = "read_at")
	private Instant readAt;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	protected Notification() {
	}

	public Notification(Long userId, String type, String message) {
		this.userId = userId;
		this.type = type;
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public Instant getReadAt() {
		return readAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void markRead() {
		this.readAt = Instant.now();
	}
}

package com.example.lmsbackend.lms;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "courses")
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String code;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "owner_id", nullable = false)
	private Long ownerId;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	protected Course() {
	}

	public Course(String code, String title, String description, Long ownerId) {
		this.code = code;
		this.title = title;
		this.description = description;
		this.ownerId = ownerId;
	}

	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}

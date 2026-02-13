package com.example.lmsbackend.lms;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "assignments")
public class Assignment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "course_id", nullable = false)
	private Long courseId;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "due_at")
	private Instant dueAt;

	@Column(name = "max_points", nullable = false)
	private int maxPoints = 100;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	protected Assignment() {
	}

	public Assignment(Long courseId, String title, String description, Instant dueAt, int maxPoints) {
		this.courseId = courseId;
		this.title = title;
		this.description = description;
		this.dueAt = dueAt;
		this.maxPoints = maxPoints;
	}

	public Long getId() {
		return id;
	}

	public Long getCourseId() {
		return courseId;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Instant getDueAt() {
		return dueAt;
	}

	public int getMaxPoints() {
		return maxPoints;
	}
}

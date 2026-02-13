package com.example.lmsbackend.lms;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "discussion_threads")
public class DiscussionThread {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "course_id", nullable = false)
	private Long courseId;

	@Column(nullable = false)
	private String title;

	@Column(name = "created_by", nullable = false)
	private Long createdBy;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	protected DiscussionThread() {
	}

	public DiscussionThread(Long courseId, String title, Long createdBy) {
		this.courseId = courseId;
		this.title = title;
		this.createdBy = createdBy;
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

	public Long getCreatedBy() {
		return createdBy;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}

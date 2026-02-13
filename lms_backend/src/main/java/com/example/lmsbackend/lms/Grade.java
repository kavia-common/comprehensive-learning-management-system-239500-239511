package com.example.lmsbackend.lms;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "grades")
public class Grade {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "submission_id", nullable = false, unique = true)
	private Long submissionId;

	@Column(name = "grader_id", nullable = false)
	private Long graderId;

	@Column(nullable = false)
	private int points;

	@Column(columnDefinition = "TEXT")
	private String feedback;

	@Column(name = "graded_at", nullable = false)
	private Instant gradedAt = Instant.now();

	protected Grade() {
	}

	public Grade(Long submissionId, Long graderId, int points, String feedback) {
		this.submissionId = submissionId;
		this.graderId = graderId;
		this.points = points;
		this.feedback = feedback;
	}

	public Long getId() {
		return id;
	}

	public Long getSubmissionId() {
		return submissionId;
	}

	public Long getGraderId() {
		return graderId;
	}

	public int getPoints() {
		return points;
	}

	public String getFeedback() {
		return feedback;
	}

	public Instant getGradedAt() {
		return gradedAt;
	}
}

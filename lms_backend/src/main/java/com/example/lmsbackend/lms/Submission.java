package com.example.lmsbackend.lms;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "submissions")
public class Submission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "assignment_id", nullable = false)
	private Long assignmentId;

	@Column(name = "student_id", nullable = false)
	private Long studentId;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Column(name = "submitted_at", nullable = false)
	private Instant submittedAt = Instant.now();

	protected Submission() {
	}

	public Submission(Long assignmentId, Long studentId, String content) {
		this.assignmentId = assignmentId;
		this.studentId = studentId;
		this.content = content;
	}

	public Long getId() {
		return id;
	}

	public Long getAssignmentId() {
		return assignmentId;
	}

	public Long getStudentId() {
		return studentId;
	}

	public String getContent() {
		return content;
	}

	public Instant getSubmittedAt() {
		return submittedAt;
	}

	public void setContent(String content) {
		this.content = content;
		this.submittedAt = Instant.now();
	}
}

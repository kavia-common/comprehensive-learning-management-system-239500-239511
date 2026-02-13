package com.example.lmsbackend.lms;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "enrollments")
public class Enrollment {

	public enum RoleInCourse {
		STUDENT,
		INSTRUCTOR
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "course_id", nullable = false)
	private Long courseId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "role_in_course", nullable = false, length = 30)
	private RoleInCourse roleInCourse;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	protected Enrollment() {
	}

	public Enrollment(Long courseId, Long userId, RoleInCourse roleInCourse) {
		this.courseId = courseId;
		this.userId = userId;
		this.roleInCourse = roleInCourse;
	}

	public Long getId() {
		return id;
	}

	public Long getCourseId() {
		return courseId;
	}

	public Long getUserId() {
		return userId;
	}

	public RoleInCourse getRoleInCourse() {
		return roleInCourse;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}

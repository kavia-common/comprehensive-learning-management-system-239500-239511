package com.example.lmsbackend.lms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class LmsDtos {

	public record CreateCourseRequest(
			@NotBlank @Size(min = 3, max = 50) String code,
			@NotBlank @Size(min = 3, max = 255) String title,
			String description
	) {
	}

	public record EnrollRequest(@NotNull Long userId) {
	}

	public record CreateAssignmentRequest(
			@NotBlank @Size(min = 3, max = 255) String title,
			String description,
			Instant dueAt,
			@Min(1) int maxPoints
	) {
	}

	public record SubmitRequest(@NotBlank String content) {
	}

	public record GradeRequest(
			@Min(0) int points,
			String feedback
	) {
	}

	public record CreateThreadRequest(@NotBlank String title) {
	}

	public record CreatePostRequest(@NotBlank String content) {
	}
}

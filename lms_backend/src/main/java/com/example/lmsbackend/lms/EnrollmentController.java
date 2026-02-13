package com.example.lmsbackend.lms;

import com.example.lmsbackend.lms.dto.LmsDtos;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses/{courseId}/enrollments")
@Tag(name = "Courses")
@SecurityRequirement(name = "bearerAuth")
public class EnrollmentController {

	private final LmsService lmsService;

	public EnrollmentController(LmsService lmsService) {
		this.lmsService = lmsService;
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
	@Operation(summary = "Enroll a user", description = "Enrolls the provided userId as a STUDENT into the course.")
	public Enrollment enroll(@PathVariable long courseId, @Valid @RequestBody LmsDtos.EnrollRequest req) {
		return lmsService.enroll(courseId, req.userId());
	}
}

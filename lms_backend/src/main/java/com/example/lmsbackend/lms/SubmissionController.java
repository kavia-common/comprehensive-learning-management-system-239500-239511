package com.example.lmsbackend.lms;

import com.example.lmsbackend.lms.dto.LmsDtos;
import com.example.lmsbackend.security.JwtAuthFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Assignments")
@SecurityRequirement(name = "bearerAuth")
public class SubmissionController {

	private final LmsService lmsService;

	public SubmissionController(LmsService lmsService) {
		this.lmsService = lmsService;
	}

	@PostMapping("/assignments/{assignmentId}/submission")
	@Operation(summary = "Submit assignment", description = "Creates or updates the authenticated student's submission.")
	public Submission submit(@PathVariable long assignmentId,
	                         Authentication authentication,
	                         @Valid @RequestBody LmsDtos.SubmitRequest req) {
		long studentId = ((JwtAuthFilter.Principal) authentication.getPrincipal()).userId();
		return lmsService.upsertSubmission(assignmentId, studentId, req.content());
	}

	@PostMapping("/submissions/{submissionId}/grade")
	@PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
	@Operation(summary = "Grade submission", description = "Grades a submission (instructor required).")
	public Grade grade(@PathVariable long submissionId,
	                   Authentication authentication,
	                   @Valid @RequestBody LmsDtos.GradeRequest req) {
		long graderId = ((JwtAuthFilter.Principal) authentication.getPrincipal()).userId();
		return lmsService.gradeSubmission(submissionId, graderId, req.points(), req.feedback());
	}
}

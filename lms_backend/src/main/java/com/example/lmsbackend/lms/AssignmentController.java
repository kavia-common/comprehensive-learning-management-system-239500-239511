package com.example.lmsbackend.lms;

import com.example.lmsbackend.lms.dto.LmsDtos;
import com.example.lmsbackend.security.JwtAuthFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/assignments")
@Tag(name = "Assignments")
@SecurityRequirement(name = "bearerAuth")
public class AssignmentController {

	private final LmsService lmsService;

	public AssignmentController(LmsService lmsService) {
		this.lmsService = lmsService;
	}

	@GetMapping
	@Operation(summary = "List assignments", description = "Lists assignments for a course (must be enrolled).")
	public List<Assignment> list(@PathVariable long courseId, Authentication authentication) {
		long userId = ((JwtAuthFilter.Principal) authentication.getPrincipal()).userId();
		return lmsService.listAssignments(courseId, userId);
	}

	@PostMapping
	@Operation(summary = "Create assignment", description = "Creates assignment (instructor required).")
	public Assignment create(@PathVariable long courseId,
	                         Authentication authentication,
	                         @Valid @RequestBody LmsDtos.CreateAssignmentRequest req) {
		long userId = ((JwtAuthFilter.Principal) authentication.getPrincipal()).userId();
		return lmsService.createAssignment(courseId, userId, req.title(), req.description(), req.dueAt(), req.maxPoints());
	}
}

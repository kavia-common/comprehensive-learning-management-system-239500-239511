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

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses")
@SecurityRequirement(name = "bearerAuth")
public class CourseController {

	private final LmsService lmsService;

	public CourseController(LmsService lmsService) {
		this.lmsService = lmsService;
	}

	@GetMapping("/me")
	@Operation(summary = "My courses", description = "Returns courses the authenticated user is enrolled in.")
	public List<Course> myCourses(Authentication authentication) {
		long userId = ((JwtAuthFilter.Principal) authentication.getPrincipal()).userId();
		return lmsService.myCourses(userId);
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
	@Operation(summary = "Create course", description = "Creates a course. The creator becomes the course owner and is enrolled as INSTRUCTOR.")
	public Course createCourse(Authentication authentication, @Valid @RequestBody LmsDtos.CreateCourseRequest req) {
		long ownerId = ((JwtAuthFilter.Principal) authentication.getPrincipal()).userId();
		return lmsService.createCourse(req.code(), req.title(), req.description(), ownerId);
	}

	@GetMapping("/{courseId}")
	@Operation(summary = "Get course", description = "Fetch a course by ID.")
	public Course getCourse(@PathVariable long courseId) {
		return lmsService.getCourse(courseId);
	}
}

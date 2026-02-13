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
@RequestMapping("/api")
@Tag(name = "Discussion")
@SecurityRequirement(name = "bearerAuth")
public class DiscussionController {

	private final LmsService lmsService;

	public DiscussionController(LmsService lmsService) {
		this.lmsService = lmsService;
	}

	@GetMapping("/courses/{courseId}/threads")
	@Operation(summary = "List discussion threads", description = "Lists discussion threads for a course.")
	public List<DiscussionThread> threads(@PathVariable long courseId, Authentication authentication) {
		long userId = ((JwtAuthFilter.Principal) authentication.getPrincipal()).userId();
		return lmsService.listThreads(courseId, userId);
	}

	@PostMapping("/courses/{courseId}/threads")
	@Operation(summary = "Create discussion thread", description = "Creates a new thread in a course.")
	public DiscussionThread createThread(@PathVariable long courseId,
	                                     Authentication authentication,
	                                     @Valid @RequestBody LmsDtos.CreateThreadRequest req) {
		long userId = ((JwtAuthFilter.Principal) authentication.getPrincipal()).userId();
		return lmsService.createThread(courseId, userId, req.title());
	}

	@GetMapping("/threads/{threadId}/posts")
	@Operation(summary = "List posts", description = "Lists posts for a thread.")
	public List<DiscussionPost> posts(@PathVariable long threadId, Authentication authentication) {
		long userId = ((JwtAuthFilter.Principal) authentication.getPrincipal()).userId();
		return lmsService.listPosts(threadId, userId);
	}

	@PostMapping("/threads/{threadId}/posts")
	@Operation(summary = "Add post", description = "Adds a post to a thread.")
	public DiscussionPost addPost(@PathVariable long threadId,
	                              Authentication authentication,
	                              @Valid @RequestBody LmsDtos.CreatePostRequest req) {
		long userId = ((JwtAuthFilter.Principal) authentication.getPrincipal()).userId();
		return lmsService.addPost(threadId, userId, req.content());
	}
}

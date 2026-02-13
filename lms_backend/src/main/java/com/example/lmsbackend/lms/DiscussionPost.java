package com.example.lmsbackend.lms;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "discussion_posts")
public class DiscussionPost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "thread_id", nullable = false)
	private Long threadId;

	@Column(name = "author_id", nullable = false)
	private Long authorId;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	protected DiscussionPost() {
	}

	public DiscussionPost(Long threadId, Long authorId, String content) {
		this.threadId = threadId;
		this.authorId = authorId;
		this.content = content;
	}

	public Long getId() {
		return id;
	}

	public Long getThreadId() {
		return threadId;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public String getContent() {
		return content;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}

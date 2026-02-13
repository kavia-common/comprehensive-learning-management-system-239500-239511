package com.example.lmsbackend.lms.repo;

import com.example.lmsbackend.lms.DiscussionPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscussionPostRepository extends JpaRepository<DiscussionPost, Long> {
	List<DiscussionPost> findByThreadId(Long threadId);
}

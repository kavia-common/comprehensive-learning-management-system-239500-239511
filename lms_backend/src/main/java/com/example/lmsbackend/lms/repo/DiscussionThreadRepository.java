package com.example.lmsbackend.lms.repo;

import com.example.lmsbackend.lms.DiscussionThread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscussionThreadRepository extends JpaRepository<DiscussionThread, Long> {
	List<DiscussionThread> findByCourseId(Long courseId);
}

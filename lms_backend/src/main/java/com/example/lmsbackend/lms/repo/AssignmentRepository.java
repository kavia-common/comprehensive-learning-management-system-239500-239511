package com.example.lmsbackend.lms.repo;

import com.example.lmsbackend.lms.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
	List<Assignment> findByCourseId(Long courseId);
}

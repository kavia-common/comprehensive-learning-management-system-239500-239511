package com.example.lmsbackend.lms.repo;

import com.example.lmsbackend.lms.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
	List<Enrollment> findByCourseId(Long courseId);
	Optional<Enrollment> findByCourseIdAndUserId(Long courseId, Long userId);
	List<Enrollment> findByUserId(Long userId);
}

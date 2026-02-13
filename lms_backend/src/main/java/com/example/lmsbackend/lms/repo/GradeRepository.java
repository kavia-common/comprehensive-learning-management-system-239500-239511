package com.example.lmsbackend.lms.repo;

import com.example.lmsbackend.lms.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
	Optional<Grade> findBySubmissionId(Long submissionId);
}

package com.example.lmsbackend.lms.repo;

import com.example.lmsbackend.lms.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
	Optional<Course> findByCode(String code);
	List<Course> findByOwnerId(Long ownerId);
}

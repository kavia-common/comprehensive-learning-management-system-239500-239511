package com.example.lmsbackend.lms;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public class LmsRepositories {

	public interface CourseRepository extends JpaRepository<Course, Long> {
		Optional<Course> findByCode(String code);
		List<Course> findByOwnerId(Long ownerId);
	}

	public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
		List<Enrollment> findByCourseId(Long courseId);
		Optional<Enrollment> findByCourseIdAndUserId(Long courseId, Long userId);
		List<Enrollment> findByUserId(Long userId);
	}

	public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
		List<Assignment> findByCourseId(Long courseId);
	}

	public interface SubmissionRepository extends JpaRepository<Submission, Long> {
		Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
		List<Submission> findByAssignmentId(Long assignmentId);
	}

	public interface GradeRepository extends JpaRepository<Grade, Long> {
		Optional<Grade> findBySubmissionId(Long submissionId);
	}

	public interface DiscussionThreadRepository extends JpaRepository<DiscussionThread, Long> {
		List<DiscussionThread> findByCourseId(Long courseId);
	}

	public interface DiscussionPostRepository extends JpaRepository<DiscussionPost, Long> {
		List<DiscussionPost> findByThreadId(Long threadId);
	}
}

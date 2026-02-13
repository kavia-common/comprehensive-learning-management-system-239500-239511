package com.example.lmsbackend.lms;

import com.example.lmsbackend.lms.repo.*;
import com.example.lmsbackend.notify.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class LmsService {

	private final CourseRepository courseRepository;
	private final EnrollmentRepository enrollmentRepository;
	private final AssignmentRepository assignmentRepository;
	private final SubmissionRepository submissionRepository;
	private final GradeRepository gradeRepository;
	private final DiscussionThreadRepository threadRepository;
	private final DiscussionPostRepository postRepository;
	private final NotificationService notificationService;

	public LmsService(CourseRepository courseRepository,
	                  EnrollmentRepository enrollmentRepository,
	                  AssignmentRepository assignmentRepository,
	                  SubmissionRepository submissionRepository,
	                  GradeRepository gradeRepository,
	                  DiscussionThreadRepository threadRepository,
	                  DiscussionPostRepository postRepository,
	                  NotificationService notificationService) {
		this.courseRepository = courseRepository;
		this.enrollmentRepository = enrollmentRepository;
		this.assignmentRepository = assignmentRepository;
		this.submissionRepository = submissionRepository;
		this.gradeRepository = gradeRepository;
		this.threadRepository = threadRepository;
		this.postRepository = postRepository;
		this.notificationService = notificationService;
	}

	public Course getCourse(long courseId) {
		return courseRepository.findById(courseId).orElseThrow(() -> new EntityNotFoundException("Course not found"));
	}

	@Transactional
	public Course createCourse(String code, String title, String description, long ownerId) {
		Course course = courseRepository.save(new Course(code, title, description, ownerId));
		enrollmentRepository.save(new Enrollment(course.getId(), ownerId, Enrollment.RoleInCourse.INSTRUCTOR));
		return course;
	}

	public List<Course> myCourses(long userId) {
		return enrollmentRepository.findByUserId(userId).stream()
				.map(e -> courseRepository.findById(e.getCourseId()).orElse(null))
				.filter(c -> c != null)
				.toList();
	}

	@Transactional
	public Enrollment enroll(long courseId, long userId) {
		ensureCourseExists(courseId);
		Enrollment enrollment = enrollmentRepository.findByCourseIdAndUserId(courseId, userId)
				.orElseGet(() -> enrollmentRepository.save(new Enrollment(courseId, userId, Enrollment.RoleInCourse.STUDENT)));

		notificationService.notifyUser(userId, "ENROLLMENT", "You were enrolled in course " + courseId);
		return enrollment;
	}

	@Transactional
	public Assignment createAssignment(long courseId, long actorId, String title, String description, Instant dueAt, int maxPoints) {
		ensureInstructor(courseId, actorId);
		return assignmentRepository.save(new Assignment(courseId, title, description, dueAt, maxPoints));
	}

	public List<Assignment> listAssignments(long courseId, long actorId) {
		ensureEnrolled(courseId, actorId);
		return assignmentRepository.findByCourseId(courseId);
	}

	@Transactional
	public Submission upsertSubmission(long assignmentId, long studentId, String content) {
		Assignment assignment = assignmentRepository.findById(assignmentId)
				.orElseThrow(() -> new EntityNotFoundException("Assignment not found"));
		ensureEnrolled(assignment.getCourseId(), studentId);

		Submission submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
				.orElseGet(() -> new Submission(assignmentId, studentId, content));
		submission.setContent(content);
		submission = submissionRepository.save(submission);

		notificationService.notifyCourseInstructors(assignment.getCourseId(),
				"SUBMISSION",
				"New submission for assignment " + assignmentId + " from student " + studentId);

		return submission;
	}

	@Transactional
	public Grade gradeSubmission(long submissionId, long graderId, int points, String feedback) {
		Submission submission = submissionRepository.findById(submissionId)
				.orElseThrow(() -> new EntityNotFoundException("Submission not found"));
		Assignment assignment = assignmentRepository.findById(submission.getAssignmentId())
				.orElseThrow(() -> new EntityNotFoundException("Assignment not found"));
		ensureInstructor(assignment.getCourseId(), graderId);

		Grade grade = gradeRepository.findBySubmissionId(submissionId)
				.orElseGet(() -> new Grade(submissionId, graderId, points, feedback));
		grade = gradeRepository.save(grade);

		notificationService.notifyUser(submission.getStudentId(),
				"GRADE",
				"Your submission " + submissionId + " was graded: " + points + " points");

		return grade;
	}

	@Transactional
	public DiscussionThread createThread(long courseId, long actorId, String title) {
		ensureEnrolled(courseId, actorId);
		return threadRepository.save(new DiscussionThread(courseId, title, actorId));
	}

	public List<DiscussionThread> listThreads(long courseId, long actorId) {
		ensureEnrolled(courseId, actorId);
		return threadRepository.findByCourseId(courseId);
	}

	@Transactional
	public DiscussionPost addPost(long threadId, long actorId, String content) {
		DiscussionThread thread = threadRepository.findById(threadId)
				.orElseThrow(() -> new EntityNotFoundException("Thread not found"));
		ensureEnrolled(thread.getCourseId(), actorId);

		DiscussionPost post = postRepository.save(new DiscussionPost(threadId, actorId, content));
		notificationService.notifyCourseEnrolled(thread.getCourseId(),
				"DISCUSSION",
				"New post in thread " + threadId);
		return post;
	}

	public List<DiscussionPost> listPosts(long threadId, long actorId) {
		DiscussionThread thread = threadRepository.findById(threadId)
				.orElseThrow(() -> new EntityNotFoundException("Thread not found"));
		ensureEnrolled(thread.getCourseId(), actorId);
		return postRepository.findByThreadId(threadId);
	}

	private void ensureCourseExists(long courseId) {
		if (!courseRepository.existsById(courseId)) {
			throw new EntityNotFoundException("Course not found");
		}
	}

	private void ensureEnrolled(long courseId, long userId) {
		if (enrollmentRepository.findByCourseIdAndUserId(courseId, userId).isEmpty()) {
			throw new AccessDeniedException("Not enrolled in course");
		}
	}

	private void ensureInstructor(long courseId, long userId) {
		Course course = getCourse(courseId);
		if (course.getOwnerId() == userId) return;

		Enrollment enr = enrollmentRepository.findByCourseIdAndUserId(courseId, userId)
				.orElseThrow(() -> new AccessDeniedException("Not enrolled in course"));
		if (enr.getRoleInCourse() != Enrollment.RoleInCourse.INSTRUCTOR) {
			throw new AccessDeniedException("Instructor role required");
		}
	}
}

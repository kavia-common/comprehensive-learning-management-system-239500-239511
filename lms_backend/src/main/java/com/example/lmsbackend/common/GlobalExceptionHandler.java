package com.example.lmsbackend.common;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
			fieldErrors.put(fe.getField(), fe.getDefaultMessage());
		}
		return ResponseEntity.badRequest().body(new ApiError(
				Instant.now(),
				400,
				"VALIDATION_ERROR",
				"Request validation failed",
				fieldErrors
		));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(
				Instant.now(),
				403,
				"FORBIDDEN",
				"Access denied",
				Map.of()
		));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(
				Instant.now(),
				404,
				"NOT_FOUND",
				ex.getMessage(),
				Map.of()
		));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiError> handleConflict(DataIntegrityViolationException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(
				Instant.now(),
				409,
				"CONFLICT",
				"Data integrity violation",
				Map.of()
		));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(new ApiError(
				Instant.now(),
				400,
				"BAD_REQUEST",
				ex.getMessage(),
				Map.of()
		));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneric(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(
				Instant.now(),
				500,
				"INTERNAL_ERROR",
				"Unexpected error",
				Map.of()
		));
	}
}

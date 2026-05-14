package com.example.distributed_lovable.common_lib.error;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record ApiError(
		HttpStatus status,
		String message,
		Instant timestamp,
		@JsonInclude(Include.NON_NULL) 
		List<ApiFieldError> errors) {
	
	public ApiError(HttpStatus status, String message) {
		this(status, message, Instant.now(), null);
	}
	
	public ApiError(HttpStatus status, String message, List<ApiFieldError> errors) {
		this(status, message, Instant.now(), errors);
	}
	
}

record ApiFieldError(String field, String message) {
	
}

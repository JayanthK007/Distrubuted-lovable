package com.example.distributed_lovable.common_lib.error;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.stripe.exception.AuthenticationException;

import io.jsonwebtoken.JwtException;

@RestControllerAdvice
public class GlobalExceptionalHandler {
	
	Logger log = LoggerFactory.getLogger(GlobalExceptionalHandler.class);
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiError> handleBadRequestException(BadRequestException badRequestException) {
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, badRequestException.getMessage());
		log.error(apiError.toString(), badRequestException);
		return ResponseEntity.status(apiError.status()).body(apiError);
	}
	
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
		ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getResourceName() +" with resource id" + ex.getResourceId()+ "not found");
		log.error(apiError.toString(), ex);
		return ResponseEntity.status(apiError.status()).body(apiError);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
		List<ApiFieldError> apiErrors = ex.getBindingResult().getFieldErrors()
						.stream()
						.map(error -> new ApiFieldError(error.getField(), error.getDefaultMessage()))
						.collect(Collectors.toList());
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,  "Input Validation Failed", apiErrors);
		log.error(apiError.toString(), ex);
		return ResponseEntity.status(apiError.status()).body(apiError);
	}
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
		ApiError apiError = new ApiError(HttpStatus.NOT_FOUND,  "Username Not Found with user name: "+ ex.getMessage());
		log.error(apiError.toString(), ex);
		return ResponseEntity.status(apiError.status()).body(apiError);
	}
	
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex) {
		ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,  "Authentication failed "+ ex.getMessage());
		log.error(apiError.toString(), ex);
		return ResponseEntity.status(apiError.status()).body(apiError);
	}
	
	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ApiError> handleJwtExceptionn(JwtException ex) {
		ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,  "Invalid JWT token "+ ex.getMessage());
		log.error(apiError.toString(), ex);
		return ResponseEntity.status(apiError.status()).body(apiError);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
		ApiError apiError = new ApiError(HttpStatus.FORBIDDEN,  "Invalid JWT token "+ ex.getMessage());
		log.error(apiError.toString(), ex);
		return ResponseEntity.status(apiError.status()).body(apiError);
	}
}

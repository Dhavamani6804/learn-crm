package com.dhava.crmdemo.exception;

import com.dhava.crmdemo.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException e) {
        ApiResponse<Void> response = ApiResponse.notFound(e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiResponse<String>> handleUserAlreadyExistException(UserAlreadyExistException e) {
        ApiResponse<String> response = ApiResponse.conflict(e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    @ExceptionHandler(LeadNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleLeadNotFoundException(LeadNotFoundException e) {
        ApiResponse<Void> response = ApiResponse.notFound(e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(LeadAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Void>> handleLeadAlreadyExistException(LeadAlreadyExistException e) {
        ApiResponse<Void> response = ApiResponse.conflict(e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}

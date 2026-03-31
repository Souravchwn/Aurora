package com.sourav.aurora.exception;

import com.sourav.aurora.constants.ApplicationConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for Aurora News Service
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NewsProviderException.class)
    public ResponseEntity<Map<String, Object>> handleNewsProviderException(
            NewsProviderException ex, WebRequest request) {
        
        log.error("News provider error: {} - {}", ex.getProviderName(), ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
                ApplicationConstants.STATUS_ERROR,
                ex.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                request.getDescription(false)
        );
        
        errorResponse.put("provider", ex.getProviderName());
        errorResponse.put("errorCode", ex.getErrorCode());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.error("Validation error: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
                ApplicationConstants.STATUS_ERROR,
                ApplicationConstants.ERROR_INVALID_PARAMETERS,
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false)
        );
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        
        errorResponse.put("fieldErrors", fieldErrors);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindException(
            BindException ex, WebRequest request) {
        
        log.error("Binding error: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
                ApplicationConstants.STATUS_ERROR,
                ApplicationConstants.ERROR_INVALID_PARAMETERS,
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        log.error("Illegal argument: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
                ApplicationConstants.STATUS_ERROR,
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
                ApplicationConstants.STATUS_ERROR,
                ApplicationConstants.ERROR_INTERNAL_SERVER,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private Map<String, Object> createErrorResponse(String status, String message, int code, String path) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status);
        errorResponse.put("message", message);
        errorResponse.put("code", code);
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("path", path);
        return errorResponse;
    }
}
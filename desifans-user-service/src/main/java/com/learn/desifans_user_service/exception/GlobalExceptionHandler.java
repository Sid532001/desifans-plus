package com.learn.desifans_user_service.exception;

import com.learn.desifans_user_service.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(ApiResponse.ErrorDetails.builder()
                                .code("USER_NOT_FOUND")
                                .message(ex.getMessage())
                                .requestId(UUID.randomUUID().toString())
                                .build())
                        .build());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(ApiResponse.ErrorDetails.builder()
                                .code("INVALID_CREDENTIALS")
                                .message(ex.getMessage())
                                .requestId(UUID.randomUUID().toString())
                                .build())
                        .build());
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountLockedException(AccountLockedException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.LOCKED)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(ApiResponse.ErrorDetails.builder()
                                .code("ACCOUNT_LOCKED")
                                .message(ex.getMessage())
                                .requestId(UUID.randomUUID().toString())
                                .build())
                        .build());
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailNotVerifiedException(EmailNotVerifiedException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(ApiResponse.ErrorDetails.builder()
                                .code("EMAIL_NOT_VERIFIED")
                                .message(ex.getMessage())
                                .requestId(UUID.randomUUID().toString())
                                .build())
                        .build());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleTokenExpiredException(TokenExpiredException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(ApiResponse.ErrorDetails.builder()
                                .code("TOKEN_EXPIRED")
                                .message(ex.getMessage())
                                .requestId(UUID.randomUUID().toString())
                                .build())
                        .build());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTokenException(InvalidTokenException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(ApiResponse.ErrorDetails.builder()
                                .code("INVALID_TOKEN")
                                .message(ex.getMessage())
                                .requestId(UUID.randomUUID().toString())
                                .build())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .error(ApiResponse.ErrorDetails.builder()
                                .code("VALIDATION_ERROR")
                                .message("Validation failed")
                                .requestId(UUID.randomUUID().toString())
                                .build())
                        .data(errors)
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(ApiResponse.ErrorDetails.builder()
                                .code("INVALID_ARGUMENT")
                                .message(ex.getMessage())
                                .requestId(UUID.randomUUID().toString())
                                .build())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(ApiResponse.ErrorDetails.builder()
                                .code("INTERNAL_SERVER_ERROR")
                                .message("An unexpected error occurred")
                                .requestId(UUID.randomUUID().toString())
                                .build())
                        .build());
    }
}

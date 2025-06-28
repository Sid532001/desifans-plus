package com.learn.desifans_user_service.controller;

import com.learn.desifans_user_service.dto.ApiResponse;
import com.learn.desifans_user_service.dto.AuthenticationResponse;
import com.learn.desifans_user_service.dto.LoginRequest;
import com.learn.desifans_user_service.dto.RefreshTokenRequest;
import com.learn.desifans_user_service.dto.UserRegistrationRequest;
import com.learn.desifans_user_service.model.User;
import com.learn.desifans_user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    /**
     * User registration
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Registration attempt for username: {}", request.getUsername());
        
        try {
            userService.registerUser(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<String>builder()
                            .success(true)
                            .message("User registered successfully. Please check your email for verification.")
                            .data("Registration successful")
                            .build());
                    
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .error(ApiResponse.ErrorDetails.builder()
                                    .code("REGISTRATION_ERROR")
                                    .message(e.getMessage())
                                    .build())
                            .build());
        }
    }

    /**
     * Token refresh - basic implementation
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh attempt");
        
        try {
            userService.refreshToken(request.getRefreshToken());
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Token refreshed successfully")
                    .data("Token refreshed")
                    .build());
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .error(ApiResponse.ErrorDetails.builder()
                                    .code("TOKEN_REFRESH_ERROR")
                                    .message(e.getMessage())
                                    .build())
                            .build());
        }
    }

    /**
     * Test endpoint to verify email (for development/testing only)
     */
    @PostMapping("/verify-email/{username}")
    public ResponseEntity<ApiResponse<String>> verifyEmailForTesting(@PathVariable String username) {
        log.info("Email verification test for user: {}", username);
        
        try {
            // Find user by username
            User user = userService.findUserByUsername(username);
            
            // Verify email
            userService.verifyEmail(user.getId(), "test-token");
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Email verified successfully")
                    .data("Email verification successful")
                    .build());
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .error(ApiResponse.ErrorDetails.builder()
                                    .code("VERIFICATION_ERROR")
                                    .message(e.getMessage())
                                    .build())
                            .build());
        }
    }

    /**
     * User logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) {
        log.info("Logout attempt");
        
        try {
            String token = authHeader.replace("Bearer ", "");
            userService.logout(token);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Logged out successfully")
                    .data("Logout successful")
                    .build());
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .error(ApiResponse.ErrorDetails.builder()
                                    .code("LOGOUT_ERROR")
                                    .message(e.getMessage())
                                    .build())
                            .build());
        }
    }

    /**
     * User login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        log.info("Login attempt for user: {}", request.getLoginIdentifier());
        
        try {
            // Get client IP and User-Agent
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            
            UserService.AuthenticationResult result = userService.authenticateUser(
                request.getLoginIdentifier(),
                request.getPassword(),
                ipAddress,
                userAgent != null ? userAgent : "Unknown"
            );
            
            AuthenticationResponse authResponse = new AuthenticationResponse(
                result.getAccessToken(),
                result.getRefreshToken(),
                3600, // 1 hour
                result.getUser()
            );
            
            return ResponseEntity.ok(ApiResponse.<AuthenticationResponse>builder()
                    .success(true)
                    .message("Login successful")
                    .data(authResponse)
                    .build());
                    
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getLoginIdentifier(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<AuthenticationResponse>builder()
                            .success(false)
                            .error(ApiResponse.ErrorDetails.builder()
                                    .code("LOGIN_FAILED")
                                    .message("Invalid credentials or account issue")
                                    .build())
                            .build());
        }
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
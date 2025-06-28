package com.learn.desifans_user_service.controller;

import com.learn.desifans_user_service.dto.ApiResponse;
import com.learn.desifans_user_service.dto.ChangePasswordRequest;
import com.learn.desifans_user_service.dto.UserProfileUpdateRequest;
import com.learn.desifans_user_service.model.User;
import com.learn.desifans_user_service.security.SecurityUtils;
import com.learn.desifans_user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Changing password for user: {}", currentUserId);
        
        userService.changePassword(
                currentUserId, 
                request.getCurrentPassword(), 
                request.getNewPassword()
        );
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Password changed successfully")
                .data("Password changed")
                .build());
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getCurrentUserProfile() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Getting profile for user: {}", currentUserId);
        
        // Use non-cached method to avoid Redis connection issues
        User user = userService.findUserByIdNoCache(currentUserId);
        
        return ResponseEntity.ok(ApiResponse.<User>builder()
                .success(true)
                .message("Profile retrieved successfully")
                .data(user)
                .build());
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateUserProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Updating profile for user: {}", currentUserId);
        
        User updatedUser = userService.updateUserProfile(currentUserId, request);
        
        return ResponseEntity.ok(ApiResponse.<User>builder()
                .success(true)
                .message("Profile updated successfully")
                .data(updatedUser)
                .build());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserProfile(@PathVariable String userId) {
        log.info("Getting profile for user ID: {}", userId);
        
        // Use non-cached method to avoid Redis connection issues
        User user = userService.findUserByIdNoCache(userId);
        
        return ResponseEntity.ok(ApiResponse.<User>builder()
                .success(true)
                .message("Profile retrieved successfully")
                .data(user)
                .build());
    }

    @DeleteMapping("/account")
    public ResponseEntity<ApiResponse<String>> deactivateAccount() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Deactivating account for user: {}", currentUserId);
        
        // Get user to find username for deactivation method
        User user = userService.findUserByIdNoCache(currentUserId);
        userService.deactivateAccount(user.getUsername());
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Account deactivated successfully")
                .data("Account deactivated")
                .build());
    }

    @PostMapping("/reactivate")
    public ResponseEntity<ApiResponse<User>> reactivateAccount() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Reactivating account for user: {}", currentUserId);
        
        // Get user to find username for reactivation method
        User existingUser = userService.findUserByIdNoCache(currentUserId);
        User user = userService.reactivateAccount(existingUser.getUsername());
        
        return ResponseEntity.ok(ApiResponse.<User>builder()
                .success(true)
                .message("Account reactivated successfully")
                .data(user)
                .build());
    }
}

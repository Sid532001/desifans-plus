package com.learn.desifans_user_service.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    @NotBlank(message = "Username is required")
    @Indexed(unique = true)
    private String username;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Indexed(unique = true)
    private String email;
    
    @NotBlank(message = "Password is required")
    private String passwordHash;
    
    @NotNull(message = "Role is required")
    @Indexed
    private UserRole role;
    
    @NotNull(message = "Status is required")
    @Indexed
    private UserStatus status;
    
    private boolean emailVerified;
    
    private String phoneNumber;
    
    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;
    
    // Basic Profile
    private UserProfile profile;
    
    // Creator-specific data (only for CREATOR role)
    private CreatorProfile creatorProfile;
    
    // Security & Auth
    private SecurityInfo security;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    private LocalDateTime lastActiveAt;
    
    // Constructors
    public User() {
        this.status = UserStatus.PENDING_VERIFICATION;
        this.role = UserRole.SUBSCRIBER;
        this.emailVerified = false;
        this.security = new SecurityInfo();
        this.profile = new UserProfile();
    }
    
    public User(String username, String email, String passwordHash, LocalDate dateOfBirth) {
        this();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.dateOfBirth = dateOfBirth;
    }
    
    // Helper methods
    public boolean isCreator() {
        return this.role == UserRole.CREATOR;
    }
    
    public boolean isSubscriber() {
        return this.role == UserRole.SUBSCRIBER;
    }
    
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }
    
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }
    
    public boolean isEmailVerified() {
        return this.emailVerified;
    }
    
    public boolean isAdult() {
        if (dateOfBirth == null) return false;
        return LocalDate.now().minusYears(18).isAfter(dateOfBirth) || 
               LocalDate.now().minusYears(18).isEqual(dateOfBirth);
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public UserStatus getStatus() {
        return status;
    }
    
    public void setStatus(UserStatus status) {
        this.status = status;
    }
    
    public boolean getEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public UserProfile getProfile() {
        return profile;
    }
    
    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }
    
    public CreatorProfile getCreatorProfile() {
        return creatorProfile;
    }
    
    public void setCreatorProfile(CreatorProfile creatorProfile) {
        this.creatorProfile = creatorProfile;
    }
    
    public SecurityInfo getSecurity() {
        return security;
    }
    
    public void setSecurity(SecurityInfo security) {
        this.security = security;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getLastActiveAt() {
        return lastActiveAt;
    }
    
    public void setLastActiveAt(LocalDateTime lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }
}

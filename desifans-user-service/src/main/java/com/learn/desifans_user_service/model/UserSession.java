package com.learn.desifans_user_service.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "user_sessions")
public class UserSession {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private String sessionToken;
    private String accessToken;
    private DeviceInfo deviceInfo;
    private boolean isActive;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @Indexed
    private LocalDateTime expiresAt;
    
    private LocalDateTime lastActivity;
    
    // Constructors
    public UserSession() {
        this.isActive = true;
        this.lastActivity = LocalDateTime.now();
    }
    
    public UserSession(String userId, String sessionToken, String accessToken, DeviceInfo deviceInfo, LocalDateTime expiresAt) {
        this();
        this.userId = userId;
        this.sessionToken = sessionToken;
        this.accessToken = accessToken;
        this.deviceInfo = deviceInfo;
        this.expiresAt = expiresAt;
    }
    
    // Helper methods
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
    
    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getSessionToken() {
        return sessionToken;
    }
    
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }
    
    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
    
    public boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
}

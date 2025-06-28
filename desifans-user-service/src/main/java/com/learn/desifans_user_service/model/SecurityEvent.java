package com.learn.desifans_user_service.model;

import java.time.LocalDateTime;

public class SecurityEvent {
    
    private String eventType;
    private String description;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;
    
    // Constructors
    public SecurityEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    public SecurityEvent(String eventType, String description, String ipAddress, String userAgent) {
        this();
        this.eventType = eventType;
        this.description = description;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
    
    // Getters and Setters
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

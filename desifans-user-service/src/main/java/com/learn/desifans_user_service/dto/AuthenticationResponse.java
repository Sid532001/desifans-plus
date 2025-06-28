package com.learn.desifans_user_service.dto;

import com.learn.desifans_user_service.model.User;
import com.learn.desifans_user_service.model.UserRole;
import com.learn.desifans_user_service.model.UserStatus;

public class AuthenticationResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn; // in seconds
    private UserDto user;
    
    // Constructors
    public AuthenticationResponse() {}
    
    public AuthenticationResponse(String accessToken, String refreshToken, long expiresIn, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = UserDto.fromUser(user);
    }
    
    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public UserDto getUser() {
        return user;
    }
    
    public void setUser(UserDto user) {
        this.user = user;
    }
    
    // Inner UserDto class for response
    public static class UserDto {
        private String id;
        private String username;
        private String email;
        private UserRole role;
        private UserStatus status;
        private boolean emailVerified;
        private String displayName;
        private String profilePicture;
        
        public static UserDto fromUser(User user) {
            UserDto dto = new UserDto();
            dto.id = user.getId();
            dto.username = user.getUsername();
            dto.email = user.getEmail();
            dto.role = user.getRole();
            dto.status = user.getStatus();
            dto.emailVerified = user.getEmailVerified();
            
            if (user.getProfile() != null) {
                dto.displayName = user.getProfile().getDisplayName();
                dto.profilePicture = user.getProfile().getProfilePicture();
            }
            
            return dto;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }
        
        public UserStatus getStatus() { return status; }
        public void setStatus(UserStatus status) { this.status = status; }
        
        public boolean isEmailVerified() { return emailVerified; }
        public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
        
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        
        public String getProfilePicture() { return profilePicture; }
        public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    }
}

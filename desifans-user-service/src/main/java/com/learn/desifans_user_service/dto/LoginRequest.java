package com.learn.desifans_user_service.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    
    @NotBlank(message = "Email or username is required")
    private String loginIdentifier; // Can be email or username
    
    @NotBlank(message = "Password is required")
    private String password;
    
    // Constructors
    public LoginRequest() {}
    
    public LoginRequest(String loginIdentifier, String password) {
        this.loginIdentifier = loginIdentifier;
        this.password = password;
    }
    
    // Getters and Setters
    public String getLoginIdentifier() {
        return loginIdentifier;
    }
    
    public void setLoginIdentifier(String loginIdentifier) {
        this.loginIdentifier = loginIdentifier;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}

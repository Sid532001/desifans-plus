package com.learn.desifans_user_service.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SecurityInfo {
    
    private LocalDateTime lastLogin;
    private List<String> loginHistory;
    private int failedLoginAttempts;
    private LocalDateTime lockoutUntil;
    private boolean twoFactorEnabled;
    private String twoFactorSecret;
    private List<String> trustedDevices;
    private List<SecurityEvent> securityEvents;
    
    // Constructors
    public SecurityInfo() {
        this.loginHistory = new ArrayList<>();
        this.failedLoginAttempts = 0;
        this.twoFactorEnabled = false;
        this.trustedDevices = new ArrayList<>();
        this.securityEvents = new ArrayList<>();
    }
    
    // Helper methods
    public boolean isLocked() {
        return lockoutUntil != null && lockoutUntil.isAfter(LocalDateTime.now());
    }
    
    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
    }
    
    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.lockoutUntil = null;
    }
    
    public void lockAccount(int minutes) {
        this.lockoutUntil = LocalDateTime.now().plusMinutes(minutes);
    }
    
    // Getters and Setters
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public List<String> getLoginHistory() {
        return loginHistory;
    }
    
    public void setLoginHistory(List<String> loginHistory) {
        this.loginHistory = loginHistory;
    }
    
    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    
    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
    
    public LocalDateTime getLockoutUntil() {
        return lockoutUntil;
    }
    
    public void setLockoutUntil(LocalDateTime lockoutUntil) {
        this.lockoutUntil = lockoutUntil;
    }
    
    public boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }
    
    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }
    
    public String getTwoFactorSecret() {
        return twoFactorSecret;
    }
    
    public void setTwoFactorSecret(String twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }
    
    public List<String> getTrustedDevices() {
        return trustedDevices;
    }
    
    public void setTrustedDevices(List<String> trustedDevices) {
        this.trustedDevices = trustedDevices;
    }
    
    public List<SecurityEvent> getSecurityEvents() {
        return securityEvents;
    }
    
    public void setSecurityEvents(List<SecurityEvent> securityEvents) {
        this.securityEvents = securityEvents;
    }
}

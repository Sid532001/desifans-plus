package com.learn.desifans_user_service.model;

public class DeviceInfo {
    
    private String userAgent;
    private String ipAddress;
    private String deviceType;
    private String os;
    private String browser;
    private String deviceFingerprint;
    
    // Constructors
    public DeviceInfo() {}
    
    public DeviceInfo(String userAgent, String ipAddress) {
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        parseUserAgent(userAgent);
    }
    
    // Helper method to parse user agent string
    private void parseUserAgent(String userAgent) {
        if (userAgent == null) return;
        
        String ua = userAgent.toLowerCase();
        
        // Simple device type detection
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            this.deviceType = "MOBILE";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            this.deviceType = "TABLET";
        } else {
            this.deviceType = "DESKTOP";
        }
        
        // Simple OS detection
        if (ua.contains("windows")) {
            this.os = "Windows";
        } else if (ua.contains("mac")) {
            this.os = "macOS";
        } else if (ua.contains("linux")) {
            this.os = "Linux";
        } else if (ua.contains("android")) {
            this.os = "Android";
        } else if (ua.contains("ios") || ua.contains("iphone") || ua.contains("ipad")) {
            this.os = "iOS";
        }
        
        // Simple browser detection
        if (ua.contains("chrome")) {
            this.browser = "Chrome";
        } else if (ua.contains("firefox")) {
            this.browser = "Firefox";
        } else if (ua.contains("safari")) {
            this.browser = "Safari";
        } else if (ua.contains("edge")) {
            this.browser = "Edge";
        }
    }
    
    // Getters and Setters
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getOs() {
        return os;
    }
    
    public void setOs(String os) {
        this.os = os;
    }
    
    public String getBrowser() {
        return browser;
    }
    
    public void setBrowser(String browser) {
        this.browser = browser;
    }
    
    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }
    
    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }
}

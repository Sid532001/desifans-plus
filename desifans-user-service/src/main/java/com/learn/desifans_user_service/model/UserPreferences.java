package com.learn.desifans_user_service.model;

public class UserPreferences {
    
    private boolean emailNotifications;
    private boolean pushNotifications;
    private boolean marketingEmails;
    private String language;
    private String timezone;
    private String theme;
    private PrivacySettings privacySettings;
    
    // Constructors
    public UserPreferences() {
        this.emailNotifications = true;
        this.pushNotifications = true;
        this.marketingEmails = false;
        this.language = "en";
        this.timezone = "UTC";
        this.theme = "light";
        this.privacySettings = new PrivacySettings();
    }
    
    // Getters and Setters
    public boolean getEmailNotifications() {
        return emailNotifications;
    }
    
    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }
    
    public boolean getPushNotifications() {
        return pushNotifications;
    }
    
    public void setPushNotifications(boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }
    
    public boolean getMarketingEmails() {
        return marketingEmails;
    }
    
    public void setMarketingEmails(boolean marketingEmails) {
        this.marketingEmails = marketingEmails;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public PrivacySettings getPrivacySettings() {
        return privacySettings;
    }
    
    public void setPrivacySettings(PrivacySettings privacySettings) {
        this.privacySettings = privacySettings;
    }
}

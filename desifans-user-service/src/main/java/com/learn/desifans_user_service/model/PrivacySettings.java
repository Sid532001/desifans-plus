package com.learn.desifans_user_service.model;

public class PrivacySettings {
    
    private boolean profileVisible;
    private boolean showOnlineStatus;
    private boolean allowDirectMessages;
    private boolean showSubscriberCount;
    private boolean allowTips;
    
    // Constructors
    public PrivacySettings() {
        this.profileVisible = true;
        this.showOnlineStatus = true;
        this.allowDirectMessages = true;
        this.showSubscriberCount = false;
        this.allowTips = true;
    }
    
    // Getters and Setters
    public boolean getProfileVisible() {
        return profileVisible;
    }
    
    public void setProfileVisible(boolean profileVisible) {
        this.profileVisible = profileVisible;
    }
    
    public boolean getShowOnlineStatus() {
        return showOnlineStatus;
    }
    
    public void setShowOnlineStatus(boolean showOnlineStatus) {
        this.showOnlineStatus = showOnlineStatus;
    }
    
    public boolean getAllowDirectMessages() {
        return allowDirectMessages;
    }
    
    public void setAllowDirectMessages(boolean allowDirectMessages) {
        this.allowDirectMessages = allowDirectMessages;
    }
    
    public boolean getShowSubscriberCount() {
        return showSubscriberCount;
    }
    
    public void setShowSubscriberCount(boolean showSubscriberCount) {
        this.showSubscriberCount = showSubscriberCount;
    }
    
    public boolean getAllowTips() {
        return allowTips;
    }
    
    public void setAllowTips(boolean allowTips) {
        this.allowTips = allowTips;
    }
}

package com.learn.desifans_user_service.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    
    private String displayName;
    private String bio;
    private String profilePicture;
    private String bannerImage;
    private String location;
    private String website;
    private List<String> socialLinks;
    private boolean isVerified;
    private UserPreferences preferences;
    
    // Constructors
    public UserProfile() {
        this.socialLinks = new ArrayList<>();
        this.isVerified = false;
        this.preferences = new UserPreferences();
    }
    
    // Getters and Setters
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }
    
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    public String getBannerImage() {
        return bannerImage;
    }
    
    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public List<String> getSocialLinks() {
        return socialLinks;
    }
    
    public void setSocialLinks(List<String> socialLinks) {
        this.socialLinks = socialLinks;
    }
    
    public boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public UserPreferences getPreferences() {
        return preferences;
    }
    
    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }
}

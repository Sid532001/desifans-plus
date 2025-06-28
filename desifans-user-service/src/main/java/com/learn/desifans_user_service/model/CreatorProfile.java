package com.learn.desifans_user_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreatorProfile {
    
    private String creatorName;
    private String category;
    private String description;
    private BigDecimal subscriptionPrice;
    private String currency;
    private boolean isVerified;
    private VerificationStatus verificationStatus;
    private List<String> contentWarnings;
    private TipMenu tipMenu;
    private CreatorSettings settings;
    private CreatorStatistics statistics;
    private LocalDateTime creatorSince;
    
    // Constructors
    public CreatorProfile() {
        this.contentWarnings = new ArrayList<>();
        this.isVerified = false;
        this.verificationStatus = VerificationStatus.PENDING;
        this.currency = "USD";
        this.tipMenu = new TipMenu();
        this.settings = new CreatorSettings();
        this.statistics = new CreatorStatistics();
        this.creatorSince = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getCreatorName() {
        return creatorName;
    }
    
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getSubscriptionPrice() {
        return subscriptionPrice;
    }
    
    public void setSubscriptionPrice(BigDecimal subscriptionPrice) {
        this.subscriptionPrice = subscriptionPrice;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }
    
    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
    
    public List<String> getContentWarnings() {
        return contentWarnings;
    }
    
    public void setContentWarnings(List<String> contentWarnings) {
        this.contentWarnings = contentWarnings;
    }
    
    public TipMenu getTipMenu() {
        return tipMenu;
    }
    
    public void setTipMenu(TipMenu tipMenu) {
        this.tipMenu = tipMenu;
    }
    
    public CreatorSettings getSettings() {
        return settings;
    }
    
    public void setSettings(CreatorSettings settings) {
        this.settings = settings;
    }
    
    public CreatorStatistics getStatistics() {
        return statistics;
    }
    
    public void setStatistics(CreatorStatistics statistics) {
        this.statistics = statistics;
    }
    
    public LocalDateTime getCreatorSince() {
        return creatorSince;
    }
    
    public void setCreatorSince(LocalDateTime creatorSince) {
        this.creatorSince = creatorSince;
    }
}

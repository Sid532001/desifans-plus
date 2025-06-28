package com.learn.desifans_user_service.model;

public class CreatorSettings {
    
    private boolean allowSubscriptions;
    private boolean allowTips;
    private boolean allowCustomRequests;
    private boolean enableLiveStreaming;
    private boolean autoRenewSubscriptions;
    private int maxCustomRequestPrice;
    private String payoutSchedule;
    
    // Constructors
    public CreatorSettings() {
        this.allowSubscriptions = true;
        this.allowTips = true;
        this.allowCustomRequests = true;
        this.enableLiveStreaming = false;
        this.autoRenewSubscriptions = true;
        this.maxCustomRequestPrice = 500;
        this.payoutSchedule = "WEEKLY";
    }
    
    // Getters and Setters
    public boolean getAllowSubscriptions() {
        return allowSubscriptions;
    }
    
    public void setAllowSubscriptions(boolean allowSubscriptions) {
        this.allowSubscriptions = allowSubscriptions;
    }
    
    public boolean getAllowTips() {
        return allowTips;
    }
    
    public void setAllowTips(boolean allowTips) {
        this.allowTips = allowTips;
    }
    
    public boolean getAllowCustomRequests() {
        return allowCustomRequests;
    }
    
    public void setAllowCustomRequests(boolean allowCustomRequests) {
        this.allowCustomRequests = allowCustomRequests;
    }
    
    public boolean getEnableLiveStreaming() {
        return enableLiveStreaming;
    }
    
    public void setEnableLiveStreaming(boolean enableLiveStreaming) {
        this.enableLiveStreaming = enableLiveStreaming;
    }
    
    public boolean getAutoRenewSubscriptions() {
        return autoRenewSubscriptions;
    }
    
    public void setAutoRenewSubscriptions(boolean autoRenewSubscriptions) {
        this.autoRenewSubscriptions = autoRenewSubscriptions;
    }
    
    public int getMaxCustomRequestPrice() {
        return maxCustomRequestPrice;
    }
    
    public void setMaxCustomRequestPrice(int maxCustomRequestPrice) {
        this.maxCustomRequestPrice = maxCustomRequestPrice;
    }
    
    public String getPayoutSchedule() {
        return payoutSchedule;
    }
    
    public void setPayoutSchedule(String payoutSchedule) {
        this.payoutSchedule = payoutSchedule;
    }
}

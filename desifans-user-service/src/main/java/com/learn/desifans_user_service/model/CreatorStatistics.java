package com.learn.desifans_user_service.model;

import java.math.BigDecimal;

public class CreatorStatistics {
    
    private long subscriberCount;
    private long totalEarnings;
    private long contentCount;
    private long likeCount;
    private long viewCount;
    private double averageRating;
    private BigDecimal monthlyEarnings;
    private BigDecimal weeklyEarnings;
    
    // Constructors
    public CreatorStatistics() {
        this.subscriberCount = 0;
        this.totalEarnings = 0;
        this.contentCount = 0;
        this.likeCount = 0;
        this.viewCount = 0;
        this.averageRating = 0.0;
        this.monthlyEarnings = BigDecimal.ZERO;
        this.weeklyEarnings = BigDecimal.ZERO;
    }
    
    // Getters and Setters
    public long getSubscriberCount() {
        return subscriberCount;
    }
    
    public void setSubscriberCount(long subscriberCount) {
        this.subscriberCount = subscriberCount;
    }
    
    public long getTotalEarnings() {
        return totalEarnings;
    }
    
    public void setTotalEarnings(long totalEarnings) {
        this.totalEarnings = totalEarnings;
    }
    
    public long getContentCount() {
        return contentCount;
    }
    
    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }
    
    public long getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }
    
    public long getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }
    
    public double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
    
    public BigDecimal getMonthlyEarnings() {
        return monthlyEarnings;
    }
    
    public void setMonthlyEarnings(BigDecimal monthlyEarnings) {
        this.monthlyEarnings = monthlyEarnings;
    }
    
    public BigDecimal getWeeklyEarnings() {
        return weeklyEarnings;
    }
    
    public void setWeeklyEarnings(BigDecimal weeklyEarnings) {
        this.weeklyEarnings = weeklyEarnings;
    }
}

package com.learn.desifans_user_service.dto;

import lombok.Data;

@Data
public class UpdatePreferencesRequest {
    
    private String language;
    private String timezone;
    private String theme;
    private Boolean emailNotifications;
    private Boolean pushNotifications;
    private Boolean smsNotifications;
    private Boolean marketingEmails;
    private Boolean twoFactorEnabled;
    private Boolean autoRenewSubscriptions;
    private Boolean showOnlineStatus;
    private Boolean allowDirectMessages;
    private Boolean allowComments;
    private Boolean allowTips;
    private String contentVisibility; // PUBLIC, SUBSCRIBERS_ONLY, PRIVATE
    private Boolean adultContentEnabled;
}

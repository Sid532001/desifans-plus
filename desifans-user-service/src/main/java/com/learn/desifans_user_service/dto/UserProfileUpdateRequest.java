package com.learn.desifans_user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {

    @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
    private String displayName;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    private String profilePicture;

    private String coverPhoto;

    private LocalDate dateOfBirth;

    private String location;

    private String website;

    private String twitter;

    private String instagram;

    private String youtube;

    // Privacy settings
    private Boolean profileVisibility;
    private Boolean allowMessages;
    private Boolean showOnlineStatus;
}

package com.learn.desifans_user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;
    
    private LocalDate dateOfBirth;
    
    @Size(max = 10, message = "Gender must not exceed 10 characters")
    private String gender;
    
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;
    
    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;
    
    @Size(max = 200, message = "Website URL must not exceed 200 characters")
    private String website;
    
    @Size(max = 50, message = "Twitter handle must not exceed 50 characters")
    private String twitterHandle;
    
    @Size(max = 50, message = "Instagram handle must not exceed 50 characters")
    private String instagramHandle;
}

package com.learn.desifans_user_service.service;

import com.learn.desifans_user_service.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${app.email.verification.base-url}")
    private String baseUrl;
    
    @Value("${spring.mail.username:noreply@desifans.com}")
    private String fromEmail;
    
    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;
    
    /**
     * Send email verification email
     */
    public void sendVerificationEmail(User user) {
        if (!emailEnabled || mailSender == null) {
            log.info("Email service disabled - would send verification email to: {}", user.getEmail());
            return;
        }
        
        try {
            String verificationToken = UUID.randomUUID().toString();
            String verificationUrl = baseUrl + "/verify-email?token=" + verificationToken + "&userId=" + user.getId();
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setFrom(fromEmail);
            message.setSubject("Verify your DesiFans account");
            message.setText(buildVerificationEmailText(user.getUsername(), verificationUrl));
            
            mailSender.send(message);
            log.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", user.getEmail(), e);
        }
    }
    
    /**
     * Send password reset email
     */
    public void sendPasswordResetEmail(User user, String resetToken) {
        if (!emailEnabled || mailSender == null) {
            log.info("Email service disabled - would send password reset email to: {}", user.getEmail());
            return;
        }
        
        try {
            String resetUrl = baseUrl + "/reset-password?token=" + resetToken + "&userId=" + user.getId();
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setFrom(fromEmail);
            message.setSubject("Reset your DesiFans password");
            message.setText(buildPasswordResetEmailText(user.getUsername(), resetUrl));
            
            mailSender.send(message);
            log.info("Password reset email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", user.getEmail(), e);
        }
    }
    
    /**
     * Send welcome email after verification
     */
    public void sendWelcomeEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setFrom(fromEmail);
        message.setSubject("Welcome to DesiFans!");
        message.setText(buildWelcomeEmailText(user.getUsername()));
        
        mailSender.send(message);
    }
    
    /**
     * Build verification email text
     */
    private String buildVerificationEmailText(String username, String verificationUrl) {
        return String.format(
            "Hi %s,\n\n" +
            "Welcome to DesiFans! Please click the link below to verify your email address:\n\n" +
            "%s\n\n" +
            "This link will expire in 12 hours.\n\n" +
            "If you didn't create this account, please ignore this email.\n\n" +
            "Best regards,\n" +
            "The DesiFans Team",
            username, verificationUrl
        );
    }
    
    /**
     * Build password reset email text
     */
    private String buildPasswordResetEmailText(String username, String resetUrl) {
        return String.format(
            "Hi %s,\n\n" +
            "You requested a password reset for your DesiFans account. Click the link below to reset your password:\n\n" +
            "%s\n\n" +
            "This link will expire in 1 hour.\n\n" +
            "If you didn't request this reset, please ignore this email.\n\n" +
            "Best regards,\n" +
            "The DesiFans Team",
            username, resetUrl
        );
    }
    
    /**
     * Build welcome email text
     */
    private String buildWelcomeEmailText(String username) {
        return String.format(
            "Hi %s,\n\n" +
            "Your email has been verified successfully! Welcome to DesiFans.\n\n" +
            "You can now:\n" +
            "- Complete your profile\n" +
            "- Subscribe to creators\n" +
            "- Explore amazing content\n\n" +
            "Ready to become a creator? You can upgrade your account anytime in your profile settings.\n\n" +
            "Best regards,\n" +
            "The DesiFans Team",
            username
        );
    }
}

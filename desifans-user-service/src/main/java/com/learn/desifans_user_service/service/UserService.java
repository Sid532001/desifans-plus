package com.learn.desifans_user_service.service;

import com.learn.desifans_user_service.dto.UserRegistrationRequest;
import com.learn.desifans_user_service.dto.UserProfileUpdateRequest;
import com.learn.desifans_user_service.model.*;
import com.learn.desifans_user_service.repository.UserRepository;
import com.learn.desifans_user_service.repository.UserSessionRepository;
import com.learn.desifans_user_service.security.JwtTokenService;
import com.learn.desifans_user_service.exception.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    @Value("${app.security.rate-limiting.login.max-attempts}")
    private int maxLoginAttempts;
    
    @Value("${app.security.rate-limiting.login.lockout-duration}")
    private long lockoutDuration;
    
    @Value("${app.security.session.max-concurrent-sessions}")
    private int maxConcurrentSessions;
    
    public UserService(UserRepository userRepository, 
                      UserSessionRepository sessionRepository,
                      JwtTokenService jwtTokenService,
                      PasswordEncoder passwordEncoder,
                      EmailService emailService) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }
    
    /**
     * Register a new user
     */
    public User registerUser(UserRegistrationRequest request) {
        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Validate age requirement (18+) if date of birth is provided
        if (request.getDateOfBirth() != null && 
            LocalDate.now().minusYears(18).isBefore(request.getDateOfBirth())) {
            throw new IllegalArgumentException("Must be 18 or older to register");
        }
        
        // Create new user
        User user = new User(request.getUsername(), request.getEmail(), 
                           passwordEncoder.encode(request.getPassword()), request.getDateOfBirth());
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setRole(UserRole.SUBSCRIBER);
        
        // Set additional profile information if provided
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.getProfile().setDisplayName(request.getFullName());
        }
        if (request.getBio() != null && !request.getBio().trim().isEmpty()) {
            user.getProfile().setBio(request.getBio());
        }
        if (request.getLocation() != null && !request.getLocation().trim().isEmpty()) {
            user.getProfile().setLocation(request.getLocation());
        }
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Send verification email
        emailService.sendVerificationEmail(savedUser);
        
        return savedUser;
    }
    
    /**
     * Register a new user (legacy method for backward compatibility)
     */
    public User registerUser(String username, String email, String password, LocalDate dateOfBirth) {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        request.setConfirmPassword(password); // Same as password for legacy calls
        request.setFullName(username); // Use username as display name for legacy calls
        request.setDateOfBirth(dateOfBirth);
        
        return registerUser(request);
    }
    
    /**
     * Authenticate user login
     */
    public AuthenticationResult authenticateUser(String loginIdentifier, String password, 
                                               String ipAddress, String userAgent) {
        // Find user by email or username
        Optional<User> userOpt = userRepository.findByEmailOrUsername(loginIdentifier, loginIdentifier);
        
        if (userOpt.isEmpty()) {
            throw new InvalidCredentialsException();
        }
        
        User user = userOpt.get();
        
        // Check if account is locked
        if (user.getSecurity().isLocked()) {
            throw new AccountLockedException(user.getSecurity().getLockoutUntil());
        }
        
        // Check if account is not deleted or suspended
        if (user.getStatus() == UserStatus.DELETED || user.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalStateException("Account is not available");
        }
        
        // Verify password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new InvalidCredentialsException();
        }
        
        // Reset failed login attempts on successful login
        user.getSecurity().resetFailedAttempts();
        user.getSecurity().setLastLogin(LocalDateTime.now());
        user.setLastActiveAt(LocalDateTime.now());
        
        // Manage concurrent sessions
        manageConcurrentSessions(user.getId());
        
        // Create new session
        String sessionId = UUID.randomUUID().toString();
        DeviceInfo deviceInfo = new DeviceInfo(userAgent, ipAddress);
        
        // Generate tokens
        String accessToken = jwtTokenService.generateAccessToken(user, sessionId);
        String refreshToken = jwtTokenService.generateRefreshToken(user, sessionId);
        
        // Save session
        UserSession session = new UserSession(user.getId(), refreshToken, accessToken, 
                                            deviceInfo, LocalDateTime.now().plusDays(7));
        sessionRepository.save(session);
        
        // Update user
        userRepository.save(user);
        
        return new AuthenticationResult(accessToken, refreshToken, user);
    }
    
    /**
     * Handle failed login attempt
     */
    private void handleFailedLogin(User user) {
        SecurityInfo security = user.getSecurity();
        security.incrementFailedAttempts();
        
        if (security.getFailedLoginAttempts() >= maxLoginAttempts) {
            security.lockAccount((int) (lockoutDuration / 60000)); // Convert to minutes
        }
        
        userRepository.save(user);
    }
    
    /**
     * Manage concurrent sessions (enforce max session limit)
     */
    private void manageConcurrentSessions(String userId) {
        List<UserSession> activeSessions = sessionRepository.findActiveSessionsByUserId(userId);
        
        if (activeSessions.size() >= maxConcurrentSessions) {
            // Remove oldest sessions
            activeSessions.stream()
                    .sorted((s1, s2) -> s1.getCreatedAt().compareTo(s2.getCreatedAt()))
                    .limit(activeSessions.size() - maxConcurrentSessions + 1)
                    .forEach(session -> {
                        session.setIsActive(false);
                        sessionRepository.save(session);
                        jwtTokenService.blacklistToken(session.getSessionToken());
                    });
        }
    }
    
    /**
     * Refresh access token
     */
    public AuthenticationResult refreshToken(String refreshToken) {
        // Validate refresh token
        if (!jwtTokenService.isRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Not a refresh token");
        }
        
        String userId = jwtTokenService.getUserIdFromToken(refreshToken);
        String sessionId = jwtTokenService.getSessionIdFromToken(refreshToken);
        
        // Find user and session
        User user = findUserById(userId);
        Optional<UserSession> sessionOpt = sessionRepository.findBySessionToken(refreshToken);
        
        if (sessionOpt.isEmpty() || !sessionOpt.get().getIsActive()) {
            throw new InvalidTokenException("Invalid session");
        }
        
        UserSession session = sessionOpt.get();
        
        // Generate new access token
        String newAccessToken = jwtTokenService.generateAccessToken(user, sessionId);
        
        // Update session
        session.setAccessToken(newAccessToken);
        session.updateLastActivity();
        sessionRepository.save(session);
        
        return new AuthenticationResult(newAccessToken, refreshToken, user);
    }
    
    /**
     * Logout user (invalidate session)
     */
    public void logout(String token) {
        String sessionId = jwtTokenService.getSessionIdFromToken(token);
        
        Optional<UserSession> sessionOpt = sessionRepository.findBySessionToken(token);
        if (sessionOpt.isPresent()) {
            UserSession session = sessionOpt.get();
            session.setIsActive(false);
            sessionRepository.save(session);
            
            // Blacklist both tokens
            jwtTokenService.blacklistToken(session.getSessionToken());
            jwtTokenService.blacklistToken(session.getAccessToken());
        }
    }
    
    /**
     * Logout from all devices
     */
    public void logoutAll(String userId) {
        List<UserSession> activeSessions = sessionRepository.findActiveSessionsByUserId(userId);
        
        for (UserSession session : activeSessions) {
            session.setIsActive(false);
            sessionRepository.save(session);
            
            // Blacklist tokens
            jwtTokenService.blacklistToken(session.getSessionToken());
            jwtTokenService.blacklistToken(session.getAccessToken());
        }
    }
    
    /**
     * Get user by ID with caching
     */
    @Cacheable(value = "user_profiles", key = "#userId")
    public User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
    
    /**
     * Get user by ID without Redis caching to avoid connection issues
     */
    public User findUserByIdNoCache(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
    
    /**
     * Get user by email
     */
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));
    }
    
    /**
     * Get user by username
     */
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("username", username));
    }
    
    // User Lookup Methods
    
    @Cacheable(value = "users", key = "#username")
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }
    
    @Cacheable(value = "users", key = "#userId")
    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }
    
    @Cacheable(value = "users", key = "#email")
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
    
    public Optional<User> findByUsernameOptional(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmailOptional(String email) {
        return userRepository.findByEmail(email);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    // User Account Management Methods
    
    @CacheEvict(value = "users", key = "#username")
    public void deactivateAccount(String username) {
        User user = findByUsername(username);
        user.setStatus(UserStatus.SUSPENDED);
        user.getSecurity().setLockoutUntil(LocalDateTime.now().plusYears(100)); // Long lockout for deactivation
        
        // Invalidate all sessions
        sessionRepository.deleteByUserId(user.getId());
        
        userRepository.save(user);
    }
    
    @CachePut(value = "users", key = "#username")
    public User reactivateAccount(String username) {
        User user = findByUsername(username);
        user.setStatus(UserStatus.ACTIVE);
        user.getSecurity().setLockoutUntil(null);
        user.getSecurity().resetFailedAttempts();
        
        return userRepository.save(user);
    }
    
    /**
     * Update user profile
     */
    @CachePut(value = "user_profiles", key = "#userId")
    public User updateUserProfile(String userId, UserProfile profileUpdate) {
        User user = findUserById(userId);
        
        // Update profile fields
        UserProfile profile = user.getProfile();
        if (profileUpdate.getDisplayName() != null) {
            profile.setDisplayName(profileUpdate.getDisplayName());
        }
        if (profileUpdate.getBio() != null) {
            profile.setBio(profileUpdate.getBio());
        }
        if (profileUpdate.getLocation() != null) {
            profile.setLocation(profileUpdate.getLocation());
        }
        if (profileUpdate.getWebsite() != null) {
            profile.setWebsite(profileUpdate.getWebsite());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * Update user profile without caching to avoid Redis dependency
     */
    public User updateUserProfile(String userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Update profile fields if provided
        if (request.getDisplayName() != null) {
            user.getProfile().setDisplayName(request.getDisplayName());
        }
        if (request.getBio() != null) {
            user.getProfile().setBio(request.getBio());
        }
        if (request.getProfilePicture() != null) {
            user.getProfile().setProfilePicture(request.getProfilePicture());
        }
        if (request.getLocation() != null) {
            user.getProfile().setLocation(request.getLocation());
        }
        if (request.getWebsite() != null) {
            user.getProfile().setWebsite(request.getWebsite());
        }
        
        // Note: Other fields like coverPhoto, dateOfBirth, social links not available in current UserProfile model
        // They can be added later when the UserProfile model is extended
        
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * Verify email address
     */
    public void verifyEmail(String userId, String verificationToken) {
        User user = findUserById(userId);
        
        // Verify token (this would typically involve checking against stored verification tokens)
        // For now, we'll assume verification is successful
        
        user.setEmailVerified(true);
        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            user.setStatus(UserStatus.ACTIVE);
        }
        
        userRepository.save(user);
    }
    
    /**
     * Change password
     */
    public void changePassword(String userId, String currentPassword, String newPassword) {
        User user = findUserById(userId);
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        
        // Add security event
        SecurityEvent passwordChangeEvent = new SecurityEvent("PASSWORD_CHANGE", 
                "Password changed by user", null, null);
        user.getSecurity().getSecurityEvents().add(passwordChangeEvent);
        
        userRepository.save(user);
        
        // Invalidate all sessions for security
        logoutAll(userId);
    }
    
    /**
     * Delete user account (soft delete)
     */
    @CacheEvict(value = "user_profiles", key = "#userId")
    public void deleteUser(String userId, String reason) {
        User user = findUserById(userId);
        
        user.setStatus(UserStatus.DELETED);
        user.setUpdatedAt(LocalDateTime.now());
        
        // Add deletion event
        SecurityEvent deletionEvent = new SecurityEvent("ACCOUNT_DELETION", 
                "Account deleted: " + reason, null, null);
        user.getSecurity().getSecurityEvents().add(deletionEvent);
        
        userRepository.save(user);
        
        // Invalidate all sessions
        logoutAll(userId);
    }
    
    /**
     * Search users
     */
    public Page<User> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.searchActiveUsers(searchTerm, pageable);
    }
    
    /**
     * Get user's active sessions
     */
    public List<UserSession> getUserSessions(String userId) {
        return sessionRepository.findActiveSessionsByUserId(userId);
    }
    
    /**
     * Authentication result class
     */
    public static class AuthenticationResult {
        private final String accessToken;
        private final String refreshToken;
        private final User user;
        
        public AuthenticationResult(String accessToken, String refreshToken, User user) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.user = user;
        }
        
        // Getters
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public User getUser() { return user; }
    }
}

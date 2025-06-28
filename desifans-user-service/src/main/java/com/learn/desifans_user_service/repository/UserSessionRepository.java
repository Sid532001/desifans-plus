package com.learn.desifans_user_service.repository;

import com.learn.desifans_user_service.model.UserSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends MongoRepository<UserSession, String> {
    
    // Basic session operations
    List<UserSession> findByUserIdAndIsActive(String userId, boolean isActive);
    
    Optional<UserSession> findBySessionToken(String sessionToken);
    
    Optional<UserSession> findByAccessToken(String accessToken);
    
    // Session management
    @Query("{'userId': ?0, 'isActive': true}")
    List<UserSession> findActiveSessionsByUserId(String userId);
    
    @Query("{'userId': ?0, 'isActive': true}")
    List<UserSession> findActiveSessionsCountByUserId(String userId);
    
    // Cleanup operations
    @Query("{'expiresAt': {'$lte': ?0}}")
    List<UserSession> findExpiredSessions(LocalDateTime now);
    
    @Query("{'isActive': false, 'lastActivity': {'$lte': ?0}}")
    List<UserSession> findInactiveSessionsOlderThan(LocalDateTime cutoffDate);
    
    // Device tracking
    @Query("{'userId': ?0, 'deviceInfo.ipAddress': ?1, 'isActive': true}")
    List<UserSession> findActiveSessionsByUserIdAndIp(String userId, String ipAddress);
    
    @Query("{'userId': ?0, 'deviceInfo.deviceFingerprint': ?1}")
    List<UserSession> findSessionsByUserIdAndDeviceFingerprint(String userId, String deviceFingerprint);
    
    // Security queries
    @Query("{'lastActivity': {'$lte': ?0}, 'isActive': true}")
    List<UserSession> findStaleActiveSessions(LocalDateTime cutoffDate);
    
    // Delete operations
    void deleteByUserId(String userId);
    
    void deleteByUserIdAndIsActive(String userId, boolean isActive);
    
    @Query(value = "{'sessionToken': ?0}", delete = true)
    void deleteBySessionToken(String sessionToken);
}

package com.learn.desifans_user_service.repository;

import com.learn.desifans_user_service.model.User;
import com.learn.desifans_user_service.model.UserRole;
import com.learn.desifans_user_service.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    // Basic CRUD operations
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmailOrUsername(String email, String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    // Status and role based queries
    List<User> findByStatus(UserStatus status);
    
    List<User> findByRole(UserRole role);
    
    @Query("{'role': ?0, 'status': ?1}")
    Page<User> findByRoleAndStatus(UserRole role, UserStatus status, Pageable pageable);
    
    // Creator specific queries
    @Query("{'role': 'CREATOR', 'status': 'ACTIVE', 'creatorProfile.category': ?0}")
    Page<User> findActiveCreatorsByCategory(String category, Pageable pageable);
    
    @Query("{'role': 'CREATOR', 'status': 'ACTIVE', 'creatorProfile.isVerified': true}")
    Page<User> findVerifiedCreators(Pageable pageable);
    
    @Query("{'role': 'CREATOR', 'creatorProfile.verificationStatus': 'PENDING'}")
    List<User> findCreatorsPendingVerification();
    
    // Search functionality
    @Query("{'$or': ["
            + "{'username': {'$regex': ?0, '$options': 'i'}}, "
            + "{'profile.displayName': {'$regex': ?0, '$options': 'i'}}, "
            + "{'creatorProfile.creatorName': {'$regex': ?0, '$options': 'i'}}"
            + "], 'status': 'ACTIVE'}")
    Page<User> searchActiveUsers(String searchTerm, Pageable pageable);
    
    @Query("{'role': 'CREATOR', 'status': 'ACTIVE', '$or': ["
            + "{'username': {'$regex': ?0, '$options': 'i'}}, "
            + "{'creatorProfile.creatorName': {'$regex': ?0, '$options': 'i'}}, "
            + "{'creatorProfile.category': {'$regex': ?0, '$options': 'i'}}"
            + "]}")
    Page<User> searchActiveCreators(String searchTerm, Pageable pageable);
    
    // Activity and analytics
    @Query("{'lastActiveAt': {'$gte': ?0}}")
    List<User> findActiveUsersSince(LocalDateTime since);
    
    @Query("{'createdAt': {'$gte': ?0, '$lte': ?1}}")
    List<User> findUsersCreatedBetween(LocalDateTime start, LocalDateTime end);
    
    // Email verification
    @Query("{'emailVerified': false, 'status': 'PENDING_VERIFICATION'}")
    List<User> findUnverifiedUsers();
    
    @Query("{'emailVerified': false, 'createdAt': {'$lte': ?0}}")
    List<User> findUnverifiedUsersOlderThan(LocalDateTime cutoffDate);
    
    // Security related queries
    @Query("{'security.failedLoginAttempts': {'$gte': ?0}}")
    List<User> findUsersWithFailedAttempts(int minAttempts);
    
    @Query("{'security.lockoutUntil': {'$gte': ?0}}")
    List<User> findCurrentlyLockedUsers(LocalDateTime now);
    
    // Subscription related (for creators)
    @Query("{'role': 'CREATOR', 'status': 'ACTIVE', 'creatorProfile.subscriptionPrice': {'$gte': ?0, '$lte': ?1}}")
    Page<User> findCreatorsByPriceRange(double minPrice, double maxPrice, Pageable pageable);
    
    // Custom aggregation queries would go here for complex statistics
    
    // Maintenance queries
    @Query("{'status': 'DELETED', 'updatedAt': {'$lte': ?0}}")
    List<User> findDeletedUsersOlderThan(LocalDateTime cutoffDate);
    
    @Query("{'lastActiveAt': {'$lte': ?0}, 'status': 'ACTIVE'}")
    List<User> findInactiveUsers(LocalDateTime cutoffDate);
}

package com.learn.desifans_user_service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.learn.desifans_user_service.model.User;
import com.learn.desifans_user_service.exception.TokenExpiredException;
import com.learn.desifans_user_service.exception.InvalidTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class JwtTokenService {
    
    @Value("${app.security.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.security.jwt.access-token.expiration}")
    private long accessTokenExpiration;
    
    @Value("${app.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;
    
    private final RedisTemplate<String, String> redisTemplate;
    private final boolean redisAvailable;
    
    private static final String BLACKLIST_PREFIX = "auth:blacklist:";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH_TOKEN";
    
    public JwtTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisAvailable = testRedisConnection();
    }
    
    private boolean testRedisConnection() {
        try {
            redisTemplate.hasKey("test:connection");
            return true;
        } catch (Exception e) {
            log.warn("Redis not available, JWT blacklisting will be disabled: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Generate access token for user
     */
    public String generateAccessToken(User user, String sessionId) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            Date expirationDate = new Date(System.currentTimeMillis() + accessTokenExpiration);
            String tokenId = UUID.randomUUID().toString();
            
            return JWT.create()
                    .withSubject(user.getId())
                    .withClaim("username", user.getUsername())
                    .withClaim("email", user.getEmail())
                    .withClaim("role", user.getRole().name())
                    .withClaim("permissions", getUserPermissions(user))
                    .withClaim("sessionId", sessionId)
                    .withClaim("type", ACCESS_TOKEN_TYPE)
                    .withClaim("tokenId", tokenId)
                    .withIssuedAt(new Date())
                    .withExpiresAt(expirationDate)
                    .withIssuer("desifans-user-service")
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error creating access token", e);
        }
    }
    
    /**
     * Generate refresh token for user
     */
    public String generateRefreshToken(User user, String sessionId) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            Date expirationDate = new Date(System.currentTimeMillis() + refreshTokenExpiration);
            String tokenId = UUID.randomUUID().toString();
            
            return JWT.create()
                    .withSubject(user.getId())
                    .withClaim("sessionId", sessionId)
                    .withClaim("type", REFRESH_TOKEN_TYPE)
                    .withClaim("tokenId", tokenId)
                    .withIssuedAt(new Date())
                    .withExpiresAt(expirationDate)
                    .withIssuer("desifans-user-service")
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error creating refresh token", e);
        }
    }
    
    /**
     * Validate and decode JWT token
     */
    public DecodedJWT validateToken(String token) {
        try {
            // Check if token is blacklisted
            String tokenId = getTokenIdFromToken(token);
            if (isTokenBlacklisted(tokenId)) {
                throw new InvalidTokenException("Token has been revoked");
            }
            
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("desifans-user-service")
                    .build();
            
            DecodedJWT decodedJWT = verifier.verify(token);
            
            // Check if token is expired
            if (decodedJWT.getExpiresAt().before(new Date())) {
                throw new TokenExpiredException();
            }
            
            return decodedJWT;
        } catch (JWTVerificationException e) {
            if (e.getMessage().contains("expired")) {
                throw new TokenExpiredException();
            }
            throw new InvalidTokenException("Invalid token: " + e.getMessage());
        }
    }
    
    /**
     * Simple boolean validation for security filter
     */
    public boolean isValidToken(String token) {
        try {
            validateToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extract user ID from token
     */
    public String getUserIdFromToken(String token) {
        DecodedJWT decodedJWT = validateToken(token);
        return decodedJWT.getSubject();
    }
    
    /**
     * Extract session ID from token
     */
    public String getSessionIdFromToken(String token) {
        DecodedJWT decodedJWT = validateToken(token);
        return decodedJWT.getClaim("sessionId").asString();
    }
    
    /**
     * Extract token ID from token
     */
    public String getTokenIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("tokenId").asString();
        } catch (Exception e) {
            throw new InvalidTokenException("Cannot extract token ID");
        }
    }
    
    /**
     * Extract token type from token
     */
    public String getTokenTypeFromToken(String token) {
        DecodedJWT decodedJWT = validateToken(token);
        return decodedJWT.getClaim("type").asString();
    }
    
    /**
     * Check if token is access token
     */
    public boolean isAccessToken(String token) {
        return ACCESS_TOKEN_TYPE.equals(getTokenTypeFromToken(token));
    }
    
    /**
     * Check if token is refresh token
     */
    public boolean isRefreshToken(String token) {
        return REFRESH_TOKEN_TYPE.equals(getTokenTypeFromToken(token));
    }
    
    /**
     * Blacklist a token
     */
    public void blacklistToken(String token) {
        if (!redisAvailable) {
            log.debug("Redis not available, skipping token blacklisting");
            return;
        }
        
        try {
            String tokenId = getTokenIdFromToken(token);
            DecodedJWT decodedJWT = JWT.decode(token);
            
            // Calculate TTL based on token expiration
            long ttlSeconds = (decodedJWT.getExpiresAt().getTime() - System.currentTimeMillis()) / 1000;
            
            if (ttlSeconds > 0) {
                redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + tokenId, 
                    "blacklisted", 
                    ttlSeconds, 
                    TimeUnit.SECONDS
                );
            }
        } catch (Exception e) {
            // Log error but don't throw - blacklisting failure shouldn't break the flow
            log.warn("Error blacklisting token: {}", e.getMessage());
        }
    }
    
    /**
     * Check if token is blacklisted
     */
    public boolean isTokenBlacklisted(String tokenId) {
        if (!redisAvailable) {
            // If Redis is not available, assume token is not blacklisted
            return false;
        }
        
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + tokenId));
        } catch (Exception e) {
            // If Redis is not available, assume token is not blacklisted
            log.warn("Redis not available for token blacklist check, allowing token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get token expiration date
     */
    public LocalDateTime getTokenExpiration(String token) {
        DecodedJWT decodedJWT = validateToken(token);
        return decodedJWT.getExpiresAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
    
    /**
     * Check if token is about to expire (within 5 minutes)
     */
    public boolean isTokenAboutToExpire(String token) {
        LocalDateTime expiration = getTokenExpiration(token);
        LocalDateTime fiveMinutesFromNow = LocalDateTime.now().plusMinutes(5);
        return expiration.isBefore(fiveMinutesFromNow);
    }
    
    /**
     * Get user permissions based on role
     */
    private List<String> getUserPermissions(User user) {
        return switch (user.getRole()) {
            case ADMIN -> List.of(
                "USER_MANAGEMENT", 
                "CREATOR_VERIFICATION", 
                "CONTENT_MODERATION", 
                "ANALYTICS_VIEW",
                "SYSTEM_SETTINGS"
            );
            case CREATOR -> {
                List<String> permissions = List.of(
                    "CREATE_CONTENT", 
                    "MANAGE_SUBSCRIPTIONS", 
                    "VIEW_ANALYTICS",
                    "MANAGE_TIPS"
                );
                if (user.getCreatorProfile() != null && user.getCreatorProfile().getIsVerified()) {
                    permissions.add("LIVE_STREAM");
                    permissions.add("CUSTOM_REQUESTS");
                }
                yield permissions;
            }
            case SUBSCRIBER -> List.of(
                "VIEW_CONTENT", 
                "SUBSCRIBE", 
                "SEND_TIPS", 
                "SEND_MESSAGES"
            );
        };
    }
    
    /**
     * Extract all claims from token as a map
     */
    public java.util.Map<String, Object> getAllClaims(String token) {
        DecodedJWT decodedJWT = validateToken(token);
        return decodedJWT.getClaims().entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                    java.util.Map.Entry::getKey,
                    entry -> entry.getValue().as(Object.class)
                ));
    }
}

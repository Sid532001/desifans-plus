package com.learn.desifans_user_service.controller;

import com.learn.desifans_user_service.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Slf4j
public class HealthController {

    private final MongoTemplate mongoTemplate;
    
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    public HealthController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthStatus = new HashMap<>();
        healthStatus.put("status", "UP");
        healthStatus.put("timestamp", LocalDateTime.now());
        healthStatus.put("service", "desifans-user-service");
        
        // Check dependencies
        Map<String, String> dependencies = new HashMap<>();
        
        // Check MongoDB
        try {
            mongoTemplate.getDb().runCommand(new org.bson.Document("ping", 1));
            dependencies.put("mongodb", "UP");
        } catch (Exception e) {
            dependencies.put("mongodb", "DOWN - " + e.getMessage());
        }
        
        // Check Redis (optional)
        try {
            if (redisTemplate != null && redisTemplate.getConnectionFactory() != null) {
                redisTemplate.getConnectionFactory().getConnection().ping();
                dependencies.put("redis", "UP");
            } else {
                dependencies.put("redis", "DISABLED");
            }
        } catch (Exception e) {
            dependencies.put("redis", "DOWN - " + e.getMessage());
        }
        
        healthStatus.put("dependencies", dependencies);
        
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Service is healthy")
                .data(healthStatus)
                .build());
    }
    
    @GetMapping("/ready")
    public ResponseEntity<ApiResponse<Map<String, Object>>> readiness() {
        Map<String, Object> readinessStatus = new HashMap<>();
        readinessStatus.put("status", "READY");
        readinessStatus.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Service is ready")
                .data(readinessStatus)
                .build());
    }
    
    @GetMapping("/live")
    public ResponseEntity<ApiResponse<Map<String, Object>>> liveness() {
        Map<String, Object> livenessStatus = new HashMap<>();
        livenessStatus.put("status", "ALIVE");
        livenessStatus.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Service is alive")
                .data(livenessStatus)
                .build());
    }
}

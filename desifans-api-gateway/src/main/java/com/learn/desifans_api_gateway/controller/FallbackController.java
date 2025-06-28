package com.learn.desifans_api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User Service is currently unavailable. Please try again later.");
        response.put("status", "FALLBACK");
        response.put("timestamp", java.time.Instant.now().toString());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/content-service")
    public ResponseEntity<Map<String, Object>> contentServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Content Service is currently unavailable. Please try again later.");
        response.put("status", "FALLBACK");
        response.put("timestamp", java.time.Instant.now().toString());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/auth-service")
    public ResponseEntity<Map<String, Object>> authServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Authentication Service is currently unavailable. Please try again later.");
        response.put("status", "FALLBACK");
        response.put("timestamp", java.time.Instant.now().toString());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}

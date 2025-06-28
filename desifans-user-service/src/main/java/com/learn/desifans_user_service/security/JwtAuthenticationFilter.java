package com.learn.desifans_user_service.security;

import com.learn.desifans_user_service.model.User;
import com.learn.desifans_user_service.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenService jwtTokenService;
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Skip filter for public endpoints
        return path.startsWith("/auth/") || 
               path.startsWith("/health/") || 
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.equals("/error");
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userId;
            
            // Check if Authorization header exists and starts with "Bearer "
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("No valid Authorization header found for URI: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
            
            // Extract JWT token
            jwt = authHeader.substring(7);
            
            // Extract user ID from token
            userId = jwtTokenService.getUserIdFromToken(jwt);
            
            // If user ID exists and no authentication is already set
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Validate token
                if (jwtTokenService.isValidToken(jwt)) {
                    
                    // Create simple authentication without loading full user details
                    // This avoids circular dependency
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userId, // Use userId as principal
                            null, // No credentials needed
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // Default role
                        );
                    
                    // Set authentication details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("Successfully authenticated user: {} for URI: {}", userId, request.getRequestURI());
                } else {
                    log.debug("Invalid JWT token for user: {} and URI: {}", userId, request.getRequestURI());
                    // Clear any existing authentication
                    SecurityContextHolder.clearContext();
                }
            }
            
        } catch (Exception e) {
            log.error("Error processing JWT token for URI {}: {}", request.getRequestURI(), e.getMessage());
            // Clear authentication context on error
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }
}

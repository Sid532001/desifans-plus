package com.learn.desifans_user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException {
    
    public InvalidTokenException(String message) {
        super("Invalid token: " + message);
    }
}

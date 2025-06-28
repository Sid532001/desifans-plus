package com.learn.desifans_user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class EmailNotVerifiedException extends RuntimeException {
    
    public EmailNotVerifiedException() {
        super("Email address must be verified before login");
    }
    
    public EmailNotVerifiedException(String message) {
        super(message);
    }
}

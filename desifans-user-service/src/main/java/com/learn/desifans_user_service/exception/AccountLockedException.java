package com.learn.desifans_user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ResponseStatus(HttpStatus.LOCKED)
public class AccountLockedException extends RuntimeException {
    
    private final LocalDateTime lockExpiry;
    
    public AccountLockedException(LocalDateTime lockExpiry) {
        super("Account is locked until: " + lockExpiry);
        this.lockExpiry = lockExpiry;
    }
    
    public LocalDateTime getLockExpiry() {
        return lockExpiry;
    }
}

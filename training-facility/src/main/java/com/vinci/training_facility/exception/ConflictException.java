package com.vinci.training_facility.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.CONFLICT, reason = "Conflict occurred")
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

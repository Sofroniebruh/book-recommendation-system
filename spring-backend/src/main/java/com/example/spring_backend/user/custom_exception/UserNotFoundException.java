package com.example.spring_backend.user.custom_exception;

import com.example.spring_backend.config.custom_exceptions.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

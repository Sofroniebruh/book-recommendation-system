package com.example.spring_backend.auth.custom_exceptions;

import com.example.spring_backend.config.custom_exceptions.AuthErrorException;

public class UserAlreadyRegistered extends AuthErrorException {
    public UserAlreadyRegistered(String message) {
        super(message);
    }
}

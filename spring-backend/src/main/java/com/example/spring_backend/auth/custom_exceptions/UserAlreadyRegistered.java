package com.example.spring_backend.auth.custom_exceptions;

public class UserAlreadyRegistered extends RuntimeException {
    public UserAlreadyRegistered(String message) {
        super(message);
    }
}

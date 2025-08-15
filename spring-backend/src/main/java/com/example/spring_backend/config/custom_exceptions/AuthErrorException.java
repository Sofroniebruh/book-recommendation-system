package com.example.spring_backend.config.custom_exceptions;

public class AuthErrorException extends RuntimeException {
    public AuthErrorException(String message) {
        super(message);
    }
}

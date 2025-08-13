package com.example.spring_backend.auth.customexceptions;

public class UserAlreadyRegistered extends RuntimeException {
    public UserAlreadyRegistered(String message) {
        super(message);
    }
}

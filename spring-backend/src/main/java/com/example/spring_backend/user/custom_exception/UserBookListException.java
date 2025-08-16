package com.example.spring_backend.user.custom_exception;

public class UserBookListException extends RuntimeException{
    public UserBookListException(String message) {
        super(message);
    }
}

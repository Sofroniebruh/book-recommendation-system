package com.example.spring_backend.book_related.book.custom_exceptions;

public class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) {
        super(message);
    }
}

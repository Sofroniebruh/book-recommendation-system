package com.example.spring_backend.book_related.book.custom_exceptions;

import com.example.spring_backend.config.custom_exceptions.EntityNotFoundException;

public class BookNotFoundException extends EntityNotFoundException {
    public BookNotFoundException(String message) {
        super(message);
    }
}

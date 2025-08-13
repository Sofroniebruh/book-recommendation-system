package com.example.spring_backend.book.records;

import com.example.spring_backend.book.Book;

import java.util.UUID;

public record BookResponse(UUID id, String title, String author, String genre) {
    public static BookResponse fromEntity(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getGenre());
    }
}

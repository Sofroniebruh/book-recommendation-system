package com.example.spring_backend.book_related.book.records;

import com.example.spring_backend.book_related.book.Book;

import java.util.List;

public record BookResponse(Long id, String title, String authors) {
    public static BookResponse fromEntity(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthors());
    }
}

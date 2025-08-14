package com.example.spring_backend.book_related.book.records;

import com.example.spring_backend.book_related.book.Book;

public record BookResponse(Long id, String title, String authors, Double rating) {
    public static BookResponse fromEntity(Book book, Double rating) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthors(), rating);
    }
}

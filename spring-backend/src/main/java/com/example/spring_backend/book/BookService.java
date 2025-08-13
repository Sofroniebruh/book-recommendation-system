package com.example.spring_backend.book;

import com.example.spring_backend.book.records.BookResponse;
import com.example.spring_backend.config.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public PaginatedResponse<BookResponse> getBooks(Pageable pageable) {
        Page<Book> page = bookRepository.findAll(pageable);
        List<BookResponse> books = page.getContent()
                .stream()
                .map(BookResponse::fromEntity)
                .toList();

        return new PaginatedResponse<>(
                books,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}

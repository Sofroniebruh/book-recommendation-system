package com.example.spring_backend.book_related.book;

import com.example.spring_backend.book_related.book.custom_exceptions.BookNotFoundException;
import com.example.spring_backend.book_related.book.records.BookResponse;
import com.example.spring_backend.config.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<BookResponse>> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "") String name
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());

        return ResponseEntity.ok(bookService.getBooks(pageable, name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) throws BookNotFoundException {
        return ResponseEntity.ok(bookService.getBookResponseById(id));
    }
}

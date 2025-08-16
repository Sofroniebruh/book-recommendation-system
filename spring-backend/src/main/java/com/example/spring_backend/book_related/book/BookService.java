package com.example.spring_backend.book_related.book;

import com.example.spring_backend.book_related.book.custom_exceptions.BookNotFoundException;
import com.example.spring_backend.book_related.book.records.BookResponse;
import com.example.spring_backend.book_related.rating.RatingService;
import com.example.spring_backend.config.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService
{
    private final BookRepository bookRepository;
    private final RatingService ratingService;

    public PaginatedResponse<BookResponse> getBooks(Pageable pageable, String name) {
        Page<Book> page;
        
        if (name.isEmpty()) {
            page = bookRepository.findAll(pageable);
        } else {
            page = bookRepository.findByTitleContainingIgnoreCase(name.trim(), pageable);
        }
        
        List<BookResponse> books;

        books = page.getContent()
                .stream()
                .map(book -> BookResponse.fromEntity(book, ratingService.getAverageRatingPerBookCheckedRounded(book.getId())))
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

    public Book getBookById(Long id) throws BookNotFoundException {
        return bookRepository.findById(id)
                        .orElseThrow(() -> new BookNotFoundException("Book with id: " + id + " not found"));
    }

    public BookResponse getBookResponseById(Long id) throws BookNotFoundException {
        Book book = getBookById(id);

        return BookResponse.fromEntity(book, ratingService.getAverageRatingPerBookCheckedRounded(book.getId()));
    }
}

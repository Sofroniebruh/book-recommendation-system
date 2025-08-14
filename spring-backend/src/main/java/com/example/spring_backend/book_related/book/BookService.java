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

    public PaginatedResponse<BookResponse> getBooks(Pageable pageable) {
        Page<Book> page = bookRepository.findAll(pageable);
        List<BookResponse> books;

        books = page.getContent()
                .stream()
                .map(book -> BookResponse.fromEntity(book, ratingService.getAverageRatingPerBook(book.getId())))
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

    public BookResponse getBookById(Long id) throws BookNotFoundException {
        return BookResponse.fromEntity(
                bookRepository.findById(id)
                        .orElseThrow(() -> new BookNotFoundException("Book with id: " + id + " not found")),
                ratingService.getAverageRatingPerBook(id));
    }
}

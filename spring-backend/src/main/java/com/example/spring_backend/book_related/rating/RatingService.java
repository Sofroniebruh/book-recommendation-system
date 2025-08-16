package com.example.spring_backend.book_related.rating;

import com.example.spring_backend.book_related.book.Book;
import com.example.spring_backend.book_related.book.BookRepository;
import com.example.spring_backend.book_related.book.custom_exceptions.BookNotFoundException;
import com.example.spring_backend.book_related.rating.records.RatingRequest;
import com.example.spring_backend.config.custom_exceptions.EntityNotFoundException;
import com.example.spring_backend.user.User;
import com.example.spring_backend.user.UserRepository;
import com.example.spring_backend.user.custom_exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public Rating saveRating(RatingRequest request) throws EntityNotFoundException {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BookNotFoundException("Book with id " + request.bookId() + " not found"));
        User user = userRepository.getUserById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("User with id " + request.userId() + " not found"));

        Rating rating = Rating.builder()
                .user(user)
                .book(book)
                .rating(request.rating())
                .build();

        return ratingRepository.save(rating);
    }

    private Double getAverageRatingPerBookChecked(Long bookId) {
        return ratingRepository.getAverageRating(bookId);
    }

    public Double getAverageRatingPerBookCheckedRounded(Long bookId) {
        return Math.floor(getAverageRatingPerBookChecked(bookId) * 100) / 100;
    }
}

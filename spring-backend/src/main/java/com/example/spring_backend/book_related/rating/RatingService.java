package com.example.spring_backend.book_related.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;

    public Double getAverageRatingPerBook(Long bookId) {

        return ratingRepository.getAverageRating(bookId);
    }
}

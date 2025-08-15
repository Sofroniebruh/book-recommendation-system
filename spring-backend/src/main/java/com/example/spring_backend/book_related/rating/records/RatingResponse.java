package com.example.spring_backend.book_related.rating.records;

import com.example.spring_backend.book_related.rating.Rating;

import java.util.UUID;

public record RatingResponse(UUID id, Integer rating, Long bookId)
{
    public static RatingResponse fromEntity(Rating rating) {
        return new RatingResponse(rating.getId(), rating.getRating(), rating.getBook().getId());
    }
}

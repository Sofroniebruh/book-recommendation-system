package com.example.spring_backend.book_related.rating.records;

import jakarta.validation.constraints.*;

public record RatingRequest(
        @NotNull(message = "User id is required") @Positive(message = "User id must be positive") Long userId,
        @NotNull(message = "Book id is required") @Positive(message = "Book id must be positive") Long bookId,
        @NotNull(message = "Rating is required") @Min(value = 1, message = "Min value is 1") @Max(value = 5, message = "Max value is 5") Integer rating) {
}

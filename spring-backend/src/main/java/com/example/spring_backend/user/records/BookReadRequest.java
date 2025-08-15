package com.example.spring_backend.user.records;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookReadRequest(
        @NotNull(message = "Book id is required") @Positive(message = "Book id must be positive") Long bookId
) {
}

package com.example.spring_backend.user.records;

import com.example.spring_backend.book_related.book.records.BookResponse;
import com.example.spring_backend.user.Role;
import com.example.spring_backend.user.User;

import java.util.Set;

public record UserDto(Long id, String username, String email, Role role, Boolean isFromDataset, Set<BookResponse> readBooks) {
    public static UserDto fromEntity(User user, Set<BookResponse> readBooks) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isFromDataset(), readBooks);
    }
}

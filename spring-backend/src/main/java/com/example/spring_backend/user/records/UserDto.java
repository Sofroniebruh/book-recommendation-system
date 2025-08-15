package com.example.spring_backend.user.records;

import com.example.spring_backend.book_related.book.Book;
import com.example.spring_backend.user.Role;
import com.example.spring_backend.user.User;

import java.util.Set;

public record UserDto(Long id, String username, String email, Role role, Boolean isFromDataset, Set<Book> readBooks) {
    public static UserDto fromEntity(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isFromDataset(), user.getReadBooks());
    }
}

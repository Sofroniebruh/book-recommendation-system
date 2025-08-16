package com.example.spring_backend.auth.records;

import com.example.spring_backend.user.User;

public record AuthenticationResponse(
        Long id,
        String username,
        String email,
        String token
) {
    public static AuthenticationResponse fromEntity(User user, String token) {
        return new AuthenticationResponse(user.getId(), user.getUsername(), user.getEmail(), token);
    }
}

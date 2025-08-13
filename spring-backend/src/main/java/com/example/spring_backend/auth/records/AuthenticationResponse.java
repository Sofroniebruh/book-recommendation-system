package com.example.spring_backend.auth.records;

public record AuthenticationResponse(
        Long id,
        String username,
        String email,
        String token
) {
}

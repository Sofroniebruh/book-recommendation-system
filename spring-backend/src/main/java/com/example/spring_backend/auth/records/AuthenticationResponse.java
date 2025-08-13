package com.example.spring_backend.auth.records;

import java.util.UUID;

public record AuthenticationResponse(
        UUID id,
        String username,
        String email,
        String token
) {
}

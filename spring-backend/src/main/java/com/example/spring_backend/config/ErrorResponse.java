package com.example.spring_backend.config;

public record ErrorResponse<T>(T error) {
}

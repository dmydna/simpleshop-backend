package com.techlab.store.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        String username,
        String role
) {}
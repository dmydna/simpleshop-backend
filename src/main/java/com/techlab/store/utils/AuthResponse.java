package com.techlab.store.utils;

public record AuthResponse(
        String accessToken,
        String tokenType,
        String username,
        String role
) {}
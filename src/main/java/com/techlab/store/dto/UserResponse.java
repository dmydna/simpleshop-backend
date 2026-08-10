package com.techlab.store.dto;

public record UserResponse(
        String username,
        String role,
        Long expiredAt
) {}

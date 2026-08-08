package com.techlab.store.dto;

import java.time.Instant;



public record UserResponse(
        String username,
        String role,
        Long expiredAt
) {}

package com.techlab.store.dto;

import com.techlab.store.enums.Role;

public record UserResponse(
        String username,
        String role
) {}

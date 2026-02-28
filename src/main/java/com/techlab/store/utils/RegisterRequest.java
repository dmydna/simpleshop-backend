package com.techlab.store.utils;

public record RegisterRequest(
        String username,
        String password,
        String email,
        String firstName,
        String lastName,
        String phone,
        String address
) {}
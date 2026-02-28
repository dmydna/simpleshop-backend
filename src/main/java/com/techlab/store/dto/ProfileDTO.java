package com.techlab.store.dto;

public record ProfileDTO(
        String username,
        String image,
        String role,
        String email,
        String firstName,
        String lastName,
        String address,
        String phone
) { }

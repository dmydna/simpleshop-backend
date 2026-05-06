package com.techlab.store.dto;

public record UpdateProfileDTO(
        String image,
        String email,
        String firstName,
        String lastName,
        String address,
        String phone
) { }

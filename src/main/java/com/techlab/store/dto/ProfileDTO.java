package com.techlab.store.dto;

public record ProfileDTO(
        String id,
        String username,
        String image,
        String role,
        String email,
        String firstName,
        String lastName,
        String address,
        String city,
        String state,
        Integer zipCode,
        String phone,
        UserMeta meta
) { }

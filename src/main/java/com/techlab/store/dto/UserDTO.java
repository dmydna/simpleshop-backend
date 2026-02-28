package com.techlab.store.dto;

public record UserDTO(
        Long id,
        String username,
        String password,
        String email,
        String clientName,
        String image,
        Boolean deleted
) {}
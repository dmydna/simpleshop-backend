package com.techlab.store.dto;
import com.techlab.store.enums.Role;


public record UserDTO(
        Long id,
        String username,
        String password,
        Role role,
        String email,
        String image
) {}
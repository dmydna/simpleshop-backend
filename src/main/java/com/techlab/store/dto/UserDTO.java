package com.techlab.store.dto;
import com.techlab.store.enums.Role;

// NOTA: se mueve status a meta.status
public record UserDTO(
        String id,
        String username,
        String password,
        Role role,
        String email,
        String image,
        UserMeta meta // <-- user meta
) {}

package com.techlab.store.dto;
import com.techlab.store.enums.Role;
import com.techlab.store.enums.UserStatus;
import com.techlab.store.dto.UserMeta;

// NOTA: se mueve status a meta.status
public record UserDTO(
        Long id,
        String username,
        String password,
        UserMeta meta,
        Role role,
        String email,
        String image
) {}
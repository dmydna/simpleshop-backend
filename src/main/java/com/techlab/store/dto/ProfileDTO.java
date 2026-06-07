package com.techlab.store.dto;
import com.techlab.store.enums.UserStatus;
import com.techlab.store.dto.UserMeta;

public record ProfileDTO(
        Long id,
        String username,
        String image,
        String role,
        UserMeta meta,
        String email,
        String firstName,
        String lastName,
        String address,
        String phone
) { }

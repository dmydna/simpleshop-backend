package com.techlab.store.dto;

import java.time.LocalDateTime;


public record BanRequest(
        Integer banDays,
        Boolean isPermanent,
        String  banReason, 
        LocalDateTime bannedAt,
        LocalDateTime banExpiresAt
) {}
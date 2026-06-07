package com.techlab.store.dto;
import java.time.LocalDateTime;
import java.time.LocalDate;
import com.techlab.store.enums.UserStatus;


public record UserMeta(
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt,
    LocalDateTime bannedAt,
    LocalDateTime banExpiresAt,
    String banReason,
    UserStatus status
) {}

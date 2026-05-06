package com.techlab.store.dto;
import java.time.LocalDateTime;
import java.time.LocalDate;

public record Meta(
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt,
    String status
) {}

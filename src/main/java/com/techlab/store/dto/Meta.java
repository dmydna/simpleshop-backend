package com.techlab.store.dto;
import java.time.LocalDateTime;
import java.time.LocalDate;

public record Meta(
    LocalDate createdAt,
    LocalDate updatedAt,
    LocalDate deletedAt,
    String status
) {}

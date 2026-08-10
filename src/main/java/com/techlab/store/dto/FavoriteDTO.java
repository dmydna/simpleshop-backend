package com.techlab.store.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import java.math.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

public record FavoriteDTO (
    String id,
    LocalDateTime createdAt,
    // Listing Info. para frontend.
    String listingId,
    String image,
    String title,
    BigDecimal price
) {}
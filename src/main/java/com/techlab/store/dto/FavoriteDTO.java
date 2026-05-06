package com.techlab.store.dto;

import jakarta.validation.constraints.NotNull;
import java.math.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

public record FavoriteDTO (
    Long id,
    LocalDateTime createdAt,
    // Listing Info. para frontend.
    Long listingId,
    String image,
    String title,
    Double price
) {}
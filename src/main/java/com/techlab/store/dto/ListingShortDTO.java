package com.techlab.store.dto;

import jakarta.validation.constraints.NotNull;
import java.math.*;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.LocalDate;

public record ListingShortDTO (
    Long id,
    LocalDateTime createdAt,
    // Listing Info.
    Long listingId,
    String image,
    String title,
    Double price
) {}
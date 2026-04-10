package com.techlab.store.dto;

public record PendingReviewDTO(
    Long id,
    Long productId,
    Long userId,
    Long listingId,
    // Para el frontend.
    String image, 
    String title
) {}

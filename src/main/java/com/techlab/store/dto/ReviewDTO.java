package com.techlab.store.dto;

import com.techlab.store.entity.Listing;

import java.time.LocalDateTime;

public record ReviewDTO(
        Long id,
        String reviewerName,
        String reviewerEmail,
        Double rating,
        String comment,
        Long productId
) { }

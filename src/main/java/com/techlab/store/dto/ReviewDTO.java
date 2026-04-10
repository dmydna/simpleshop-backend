package com.techlab.store.dto;

import com.techlab.store.entity.Listing;

import java.time.LocalDateTime;

public record ReviewDTO(
        Long id,
        Integer rating,
        String comment,
        LocalDateTime date,
        String reviewerName,
        String reviewerEmail,
        Long productId
) { }

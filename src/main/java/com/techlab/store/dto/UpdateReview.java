package com.techlab.store.dto;

import com.techlab.store.entity.Listing;
import com.techlab.store.enums.ReviewStatus;

import java.time.LocalDateTime;

public record UpdateReview(
        String id,
        ReviewStatus status,
        Double rating,
        String comment
) { }

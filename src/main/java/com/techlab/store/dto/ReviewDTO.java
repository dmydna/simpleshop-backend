package com.techlab.store.dto;

import com.techlab.store.entity.Listing;
import com.techlab.store.enums.ReviewStatus;

import java.time.LocalDateTime;

public record ReviewDTO(
        Long id,
        String username,
        String userPic,
        Double rating,
        String comment,
        Long productId,
        Long listingId,
        Meta meta
) { }

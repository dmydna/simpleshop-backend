package com.techlab.store.dto;

public record ReviewDTO(
        String id,
        String username,
        String userPic,
        Double rating,
        String comment,
        String productId,
        String listingId,
        Meta meta
) { }

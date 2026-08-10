package com.techlab.store.dto;

import java.math.BigDecimal;

public record ReviewRequest(
    String id,
    String listingId,
    String title,
    BigDecimal price,
    String thumbnail
) {}

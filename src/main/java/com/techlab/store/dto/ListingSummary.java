package com.techlab.store.dto;

import java.math.BigDecimal;
import java.util.List;

import com.techlab.store.enums.ListingStatus;
import com.techlab.store.model.ProductDimensions;

public record ListingSummary (
    Long id,
    String title,
    BigDecimal price,
    BigDecimal finalPrice,
    Integer discountPercentage,
    String  availabilityStatus,
    String  thumbnail,
    String  hash,
    Integer stock,
    List<String> tags,
    Meta meta
){}

package com.techlab.store.dto;

import java.math.BigDecimal;
import java.util.List;

public record ListingSummary (
    String id,
    String title,
    BigDecimal price,
    BigDecimal finalPrice,
    Integer discountPercentage,
    String  availabilityStatus,
    String  thumbnail,
    Integer stock,
    List<String> tags,
    Meta meta
){}

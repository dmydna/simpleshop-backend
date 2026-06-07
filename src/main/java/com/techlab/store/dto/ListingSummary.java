package com.techlab.store.dto;

import java.util.List;

import com.techlab.store.enums.Status;
import com.techlab.store.model.ProductDimensions;

public record ListingSummary (
    Long id,
    String title,
    Double price,
    Double discountPercentage,
    String availabilityStatus,
    String thumbnail,
    String hash,
    Integer stock,
    List<String> tags,
    Meta meta
){}

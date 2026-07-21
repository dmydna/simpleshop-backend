package com.techlab.store.dto;

import java.math.BigDecimal;
import java.util.List;

public record ListingDraftDTO (
    // Listing
    Long id,
    String title,
    String description,
    BigDecimal price,
    BigDecimal finalPrice,
    Integer stock,
    Integer discountPercentage,
    Double rating,
    String warrantyInformation,
    String shippingInformation,
    String availabilityStatus,
    String returnPolicy,
    Integer minimumOrderQuantity,
    List<String> images,
    List<String> tags,
    String thumbnail,
    String hash,
    // product
    String sku,
    Meta meta
){}

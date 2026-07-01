package com.techlab.store.dto;

import java.util.List;

import com.techlab.store.enums.ListingStatus;
import com.techlab.store.model.ProductDimensions;

public record ListingDraftDTO (
    // Listing
    Long id,
    String title,
    String description,
    Double price,
    Integer stock,
    Double discountPercentage,
    Double rating,
    String warrantyInformation,
    String shippingInformation,
    String availabilityStatus,
    String returnPolicy,
    Integer minimumOrderQuantity,
    List<String> images,
    String thumbnail,
    String hash,
    // product
    String sku,
    Meta meta
){}

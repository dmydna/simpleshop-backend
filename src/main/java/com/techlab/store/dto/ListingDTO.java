package com.techlab.store.dto;

import java.util.List;

import com.techlab.store.enums.Status;
import com.techlab.store.model.ProductDimensions;

public record ListingDTO (
    // Listing
    Long id,
    String title,
    String description,
    Double price,
    Double discountPercentage,
    Double rating,
    String warrantyInformation,
    String shippingInformation,
    String availabilityStatus,
    List<ReviewDTO> reviews,
    String returnPolicy,
    Integer minimumOrderQuantity,
    List<String> images,
    String thumbnail,
    String hash,
    Status status,
    // product
    Long productId,
    String productName,
    String sku,
    String brand,
    Integer weight,
    ProductDimensions dimensions,
    Integer stock,
    String category,
    List<String> tags,
    Meta meta
){}

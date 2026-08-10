package com.techlab.store.dto;

import java.math.BigDecimal;
import java.util.List;
import com.techlab.store.model.ProductDimensions;

public record ListingDTO (
    // Listing
    String id,
    String title,
    String description,
    BigDecimal price,
    BigDecimal finalPrice,
    Integer discountPercentage,
    Double rating,
    String warrantyInformation,
    String shippingInformation,
    String availabilityStatus,
    List<ReviewDTO> reviews,
    String returnPolicy,
    Integer minimumOrderQuantity,
    List<String> images,
    String thumbnail,
    // product
    String productId,
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

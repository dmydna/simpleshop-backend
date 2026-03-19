package com.techlab.store.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.techlab.store.entity.Review;
import com.techlab.store.enums.Visibility;
import com.techlab.store.model.ProductDimensions;
import com.techlab.store.model.ProductMeta;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public record ListingDTO (
    // Listing
    Long id,
    String title,
    String description,
    Double price,
    Visibility visibility,

    LocalDate deletedDate,
    LocalDate createdDate,
    LocalDate modifiedDate,

    Double discountPercentage,
    Double rating,
    String warrantyInformation,
    String shippingInformation,
    String availabilityStatus,
    Set<Review> reviews,
    String returnPolicy,
    Integer minimumOrderQuantity,
    List<String> images,
    String thumbnail,
    String hash,
    Boolean deleted,
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
    ProductMeta meta
){}

package com.techlab.store.dto;

import com.techlab.store.entity.Review;
import com.techlab.store.model.ProductDimensions;
import com.techlab.store.model.ProductMeta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListingDTO {

    // Listing
    private Long id;
    private String title;
    @Column(length = 1000)
    private String description;
    private Double price;
    private LocalDate deletedDate;
    private Double discountPercentage;
    private Double rating;
    private String warrantyInformation;
    private String shippingInformation;
    private String availabilityStatus;
    private Set<Review> reviews = new HashSet<>();
    private String returnPolicy;
    private Integer minimumOrderQuantity;
    @ElementCollection
    private List<String> images;
    private String thumbnail;

    // product
    private Long product_id;
    private String product_name;
    private String sku;
    private String brand;
    private Integer weight;
    @Embedded
    private ProductDimensions dimensions;
    private Integer stock;
    private String category;
    @ElementCollection
    private List<String> tags;
    @Embedded
    private ProductMeta meta;
//    private List<Long> listings;
}

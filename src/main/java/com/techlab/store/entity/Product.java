package com.techlab.store.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.techlab.store.model.ProductDimensions;
import com.techlab.store.model.ProductMeta;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;
    private String title;
    @Column(length = 1000)
    private String description;
    private Double price;
    private String category;
    private Integer stock;
    private Boolean deleted;
    private LocalDate deletedDate;
    private Double discountPercentage;
    private Double rating;
    @ElementCollection
    private List<String> tags;
    private String brand;
    private String sku;
    private Integer weight;
    @Embedded
    private ProductDimensions dimensions;
    private String warrantyInformation;
    private String shippingInformation;
    private String availabilityStatus;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Review> reviews = new HashSet<>();
    private String returnPolicy;
    private Integer minimumOrderQuantity;
    @Embedded
    private ProductMeta meta;
    @ElementCollection
    private List<String> images;
    private String thumbnail;
}

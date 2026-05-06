package com.techlab.store.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.techlab.store.enums.Status;
import com.techlab.store.model.ProductDimensions;
import com.techlab.store.model.ProductMeta;

import jakarta.persistence.EnumType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Product {
    
    // TODO actualizar meta a LocalDataTime

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Data
    private String name;
    @Column(unique = true, nullable = false)
    private String sku;
    private String brand;
    private Integer weight;
    private Double rating;
    @Embedded
    private ProductMeta meta;
    @Embedded
    private ProductDimensions dimensions;
    @ElementCollection
    private List<String> tags;

    // status
    @Enumerated(EnumType.STRING)
    Status status = Status.ACTIVE;

    // Meta
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<Listing> listings;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PendingReview> pendingReviews = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Review> reviews = new ArrayList<>();

}

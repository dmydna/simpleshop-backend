package com.techlab.store.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.techlab.store.enums.Status;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
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

@Entity
@Getter
@Setter
public class Listing {

    // TODO actualizar meta a LocalDataTime

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Data
    private String title;
    @Column(length = 1000)
    private String description;
    private Double price;
    private Integer stock;
    
    private Double discountPercentage;
    private String warrantyInformation;
    private String shippingInformation;
    private String availabilityStatus;
    private String returnPolicy;
    private Integer minimumOrderQuantity;
    @ElementCollection
    private List<String> images = new ArrayList<>();
    private String thumbnail;
    @Column(unique = true, nullable = false, updatable = false)
    private String hash;

    // Status
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    // Meta
    private LocalDate updatedAt;
    private LocalDate deletedAt;
    private LocalDate createdAt = LocalDate.now();

    // Relations
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties("listings")
    private Product product;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<PendingReview> pendingReviews = new ArrayList<>();
}

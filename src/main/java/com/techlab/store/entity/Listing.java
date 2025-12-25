package com.techlab.store.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(length = 1000)
    private String description;
    private Double price;
    private Boolean deleted;
    private LocalDate deletedDate;
    private Double discountPercentage;
    private Double rating;
    private String warrantyInformation;
    private String shippingInformation;
    private String availabilityStatus;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Review> reviews = new HashSet<>();
    private String returnPolicy;
    private Integer minimumOrderQuantity;
    @ElementCollection
    private List<String> images;
    private String thumbnail;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}


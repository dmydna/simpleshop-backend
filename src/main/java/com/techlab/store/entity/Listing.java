package com.techlab.store.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.techlab.store.enums.ListingStatus;

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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "LISTINGS")
public class Listing extends BaseEntity {

    // Status
    @Enumerated(EnumType.STRING)
    private ListingStatus status = ListingStatus.ACTIVE;

    // Data
    private String title;
    @Column(length = 1000)
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Integer visits = 0; 
    private Integer discountPercentage = 0;
    private String warrantyInformation;
    private String shippingInformation;
    private String availabilityStatus;
    private String returnPolicy;
    private Integer minimumOrderQuantity;
    @ElementCollection
    private List<String> images = new ArrayList<>();
    private String thumbnail;

    // Relations
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true) // <-- true si admite draft
    @JsonIgnoreProperties("listings")
    private Product product;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<Favorite> favorites = new ArrayList<>();

}

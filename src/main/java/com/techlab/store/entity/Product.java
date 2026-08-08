package com.techlab.store.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.techlab.store.enums.Status;
import com.techlab.store.model.ProductDimensions;
import com.techlab.store.model.ProductMeta;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "PRODUCTS")
public class Product extends BaseEntity{
    
    // Status
    @Enumerated(EnumType.STRING)
    Status status = Status.ACTIVE;

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
    private String category;

    // Relations
    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<Listing> listings;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Review> reviews = new ArrayList<>();

}

package com.techlab.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private String name;
    @Column(unique = true, nullable = false)
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
    boolean deleted;

//    @JsonIgnoreProperties("product")
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Listing> listings;


}

package com.techlab.store.entity;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.Getter;
import lombok.Setter;



@Entity @Getter @Setter
public class PendingReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // ID del comprador (para saber quién puede crear la review)

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product; // Relación con la publicación (no con el producto)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "reviewed", nullable = false)
    private boolean reviewed = false; // Indica si ya se creó la review

    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing; 
}

package com.techlab.store.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import java.util.List;

import com.fasterxml.jackson.annotation.*;

@Entity
@Getter
@Setter
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

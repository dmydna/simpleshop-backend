package com.techlab.store.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.techlab.store.enums.ReviewStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@ToString
@Table(name = "REVIEWS")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Data
    private Double rating;
    @Column(length = 1000)
    private String comment;

    // Status
    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.PENDING;

    // Meta
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relations

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private Long listingId; 
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;
}

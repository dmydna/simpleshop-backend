package com.techlab.store.model;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;


@Embeddable
public class ProductMeta {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String barcode;
    private String qrCode;
    // Getters y Setters
}


package com.techlab.store.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class ProductDimensions {
    private Double width;
    private Double height;
    private Double depth;
    // Getters y Setters
}
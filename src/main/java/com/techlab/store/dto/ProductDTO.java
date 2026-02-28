package com.techlab.store.dto;


public record ProductDTO(
    Long id,
    Long listingId,
    String title,
    String brand,
    Double price,
    Integer stock,
    String Category
) {}

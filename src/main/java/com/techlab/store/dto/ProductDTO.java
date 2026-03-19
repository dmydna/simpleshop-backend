package com.techlab.store.dto;


public record ProductDTO(
    Long id,
    Long listingId,
    String name,
    String brand,
    Double price,
    Integer stock,
    String category
) {}

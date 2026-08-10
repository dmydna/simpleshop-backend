package com.techlab.store.dto;

import java.util.List;

import com.techlab.store.model.ProductDimensions;


public record ProductDTO(
    String id,
    String name,
    String sku,
    String brand,
    Integer weight,
    Meta meta,
    Double rating, // asignar un rating inicial?
    ProductDimensions dimensions,
    String category, // formato "parent / child / subchild"
    List<String> tags
) {}

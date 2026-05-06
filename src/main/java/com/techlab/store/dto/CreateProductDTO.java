package com.techlab.store.dto;

import java.util.List;

import com.techlab.store.model.ProductDimensions;

public record CreateProductDTO(
    String name,
    String sku,
    String brand,
    Integer weight,
    ProductDimensions dimensions,
    // formato "parent / child / subchild"
    String category, 
    List<String> tags
){}

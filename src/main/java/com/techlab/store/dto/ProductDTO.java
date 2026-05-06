package com.techlab.store.dto;

import com.techlab.store.model.ProductDimensions;
import java.util.List;


public record ProductDTO(
    Long id,
    String name,
    String sku,
    String brand,
    Integer weight,
    Double rating, // asignar un rating inicial?
    ProductDimensions dimensions,
    String category, // formato "parent / child / subchild"
    List<String> tags,
    Meta meta
) {}

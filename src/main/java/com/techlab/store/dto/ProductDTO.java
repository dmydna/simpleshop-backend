package com.techlab.store.dto;

import com.techlab.store.model.ProductDimensions;
import com.techlab.store.enums.ListingStatus;
import com.techlab.store.dto.Meta;
import java.util.List;


public record ProductDTO(
    Long id,
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

package com.techlab.store.dto;

import com.techlab.store.model.ProductDimensions;
import com.techlab.store.enums.Status;
import java.util.List;


public record ProductDraftDTO(
    Long id,
    String name,
    String sku,
    String brand,
    Integer weight,
    Status status,
    ProductDimensions dimensions,
    String category, 
    List<String> tags,
    Meta meta
) {}

package com.techlab.store.dto;

import com.techlab.store.model.ProductDimensions;
import com.techlab.store.enums.ListingStatus;
import java.util.List;


public record ProductDraftDTO(
    Long id,
    String name,
    String sku,
    String brand,
    Integer weight,
    ListingStatus status,
    ProductDimensions dimensions,
    String category, 
    List<String> tags,
    Meta meta
) {}

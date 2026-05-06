package com.techlab.store.dto;

import java.util.List;

import com.techlab.store.enums.Status;
import com.techlab.store.model.ProductDimensions;


public record UpdateProductDTO(
    String name,
    String brand,
    Integer weight,
    Status status,
    ProductDimensions dimensions,
    String category, 
    List<String> tags
){}

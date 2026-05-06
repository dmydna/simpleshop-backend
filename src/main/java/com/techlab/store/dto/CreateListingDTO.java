package com.techlab.store.dto;

import java.util.List;

public record CreateListingDTO (
    // Post 
    String       title,
    String       description,
    Double       price,
    Double       discountPercentage,
    String       warrantyInformation,
    String       shippingInformation,
    String       returnPolicy,
    Integer      minimumOrderQuantity,
    List<String> images,
    String       thumbnail,
    String       sku
){}

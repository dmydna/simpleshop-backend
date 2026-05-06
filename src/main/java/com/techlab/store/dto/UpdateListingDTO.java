package com.techlab.store.dto;

import java.util.List;

import com.techlab.store.enums.Status;

public record UpdateListingDTO (
    String       title,
    String       description,
    Double       price,
    Integer      stock,
    Status       status,
    Double       discountPercentage,
    String       warrantyInformation,
    String       shippingInformation,
    String       returnPolicy,
    Integer      minimumOrderQuantity,
    List<String> images,
    String       thumbnail
// Nota: no se debe actualizar producto desde listing
){}
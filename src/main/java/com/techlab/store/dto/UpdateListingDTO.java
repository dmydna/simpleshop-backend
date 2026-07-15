package com.techlab.store.dto;

import java.math.BigDecimal;
import java.util.List;

import com.techlab.store.enums.ListingStatus;

public record UpdateListingDTO (
    String        title,
    String        description,
    BigDecimal    price,
    Integer       stock,
    ListingStatus status,
    Integer       discountPercentage,
    String        warrantyInformation,
    String        shippingInformation,
    String        returnPolicy,
    Integer       minimumOrderQuantity,
    List<String>  images,
    String        thumbnail,
    String        sku    // <-- valido solo para Draft
// Nota: no actualizar producto desde listing
){}
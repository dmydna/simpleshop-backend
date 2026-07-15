package com.techlab.store.dto;
import com.techlab.store.enums.ListingStatus;

import java.math.BigDecimal;
import java.util.List;

public record CreateListingDTO (
    // Post 
    String       title,
    String       description,
    BigDecimal   price,
    Integer      discountPercentage,
    String       warrantyInformation,
    String       shippingInformation,
    String       returnPolicy,
    Integer      minimumOrderQuantity,
    List<String> images,
    String       thumbnail,
    String       sku,
    ListingStatus       status // <-- Valido solo para crear draft, en otros casos se ignora.
){}

package com.techlab.store.dto;

import java.math.BigDecimal;

public record OrderItemDto (
    Long id,
    Long listingId,
    String name,
    int stock,
    int quantity,
    BigDecimal priceAtPurchase // precio a pagar
){}

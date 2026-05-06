package com.techlab.store.dto;

public record OrderItemDto (
    Long id,
    Long listingId,
    String name,
    int stock,
    int quantity,
    Double priceAtPurchase // precio a pagar
){}

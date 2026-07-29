package com.techlab.store.dto;
import com.techlab.store.enums.OrderStatus;

public record OrderSummary(
    Long id,
    Long clientId,
    OrderStatus status,
    Integer totalAmount,
    Integer totalQuantity,
    Meta meta
){}

package com.techlab.store.dto;
import com.techlab.store.enums.OrderStatus;

public record OrderSummary(
    String id,
    String clientId,
    OrderStatus status,
    Integer totalAmount,
    Integer totalQuantity,
    Meta meta
){}

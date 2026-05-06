package com.techlab.store.dto;

import java.math.BigDecimal;
import java.util.List;

import com.techlab.store.entity.Order;
import com.techlab.store.enums.OrderStatus;

public record OrderComplete(
    Long id,
    OrderStatus status,
    List<OrderItemDto> details,
    List<OrderItemDto> failedItems,
    ClientDTO client, // Deprecado
    Integer totalQuantity,
    BigDecimal totalAmount
){}

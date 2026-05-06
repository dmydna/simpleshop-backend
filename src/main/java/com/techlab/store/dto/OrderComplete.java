package com.techlab.store.dto;

import java.math.BigDecimal;
import java.util.List;

import com.techlab.store.entity.Order;


public record OrderComplete(
    Long id,
    Order.OrderState stat,
    List<OrderItemDto> details,
    List<OrderItemDto> failedItems,
    ClientDTO client, // Deprecado
    Integer totalQuantity,
    BigDecimal totalAmount
){}

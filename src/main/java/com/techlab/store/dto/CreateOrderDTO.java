package com.techlab.store.dto;

import java.math.BigDecimal;
import java.util.List;

import com.techlab.store.entity.Order;
import com.techlab.store.enums.OrderStatus;

// DONE: inyectar cliente mediante auth
public record CreateOrderDTO(
    List<OrderItemDto> items,
    Integer totalQuantity,
    BigDecimal totalAmount
){}

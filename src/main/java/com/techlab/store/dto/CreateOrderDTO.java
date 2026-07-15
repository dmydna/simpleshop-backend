package com.techlab.store.dto;

import java.math.BigDecimal;
import java.util.List;



// DONE: inyectar cliente mediante auth
public record CreateOrderDTO(
    List<OrderItemDto> items,
    Integer totalQuantity,
    BigDecimal totalAmount
){}

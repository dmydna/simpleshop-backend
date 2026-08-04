package com.techlab.store.dto;

import java.math.BigDecimal;
import java.util.List;

import com.techlab.store.enums.OrderStatus;


public record OrderComplete(
    Long id,
    String transactionHash,
    String operationNumber,
    Meta meta,
    OrderStatus status,
    List<OrderItemDto> items,
    List<OrderItemDto> failedItems,
    ClientDTO client, // Deprecado
    Integer totalQuantity,
    BigDecimal totalAmount
){}

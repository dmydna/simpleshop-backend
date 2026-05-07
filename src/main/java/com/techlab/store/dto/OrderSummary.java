package com.techlab.store.dto;
import java.util.List;
import com.techlab.store.enums.OrderStatus;


public record OrderSummary(
    Long id,
    OrderStatus status,
    List<OrderItemDto> items,
    Long client_id
){}

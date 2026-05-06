package com.techlab.store.dto;

import java.util.List;

import com.techlab.store.entity.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.techlab.store.enums.OrderStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummary{
    Long id;
    OrderStatus status;
    List<OrderItemDto> details;
    Long client_id;
}

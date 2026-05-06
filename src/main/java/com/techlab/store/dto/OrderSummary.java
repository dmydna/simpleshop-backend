package com.techlab.store.dto;

import java.util.List;

import com.techlab.store.entity.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummary{
    Long id;
    Order.OrderState state;
    List<OrderItemDto> details;
    Long client_id;
}

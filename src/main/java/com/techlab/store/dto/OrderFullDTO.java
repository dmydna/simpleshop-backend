package com.techlab.store.dto;

import com.techlab.store.entity.Order;
import com.techlab.store.dto.*;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderFullDTO{
    Long id;
    Order.OrderState state;
    Set<OrderDetailDTO> details;
    ClientDTO client;
}

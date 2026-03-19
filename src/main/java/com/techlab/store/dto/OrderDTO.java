package com.techlab.store.dto;


import com.techlab.store.entity.Order;
import lombok.*;

import java.util.Set;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    Long id;
    Order.OrderState state;
    Set<OrderDetailDTO> details;
}

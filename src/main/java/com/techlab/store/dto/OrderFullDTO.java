package com.techlab.store.dto;

import com.techlab.store.entity.Order;
import com.techlab.store.dto.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderFullDTO{
    Long id;
    Order.OrderState state;
    Set<OrderDetailDTO> details;
    Set<OrderDetailDTO> failedDetails;
    ClientDTO client; // Deprecado
    Integer totalQuantity;
    BigDecimal totalAmount;
}

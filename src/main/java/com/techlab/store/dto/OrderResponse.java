package com.techlab.store.dto;

import java.math.BigDecimal;
import java.util.Set;

public record OrderResponse(
        Long orderId,
        Set<OrderDetailDTO> failed
) { }

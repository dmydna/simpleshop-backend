package com.techlab.store.dto;

import java.util.List;

public record OrderResponse(
        Long orderId,
        List<OrderItemDto> failed
) { }

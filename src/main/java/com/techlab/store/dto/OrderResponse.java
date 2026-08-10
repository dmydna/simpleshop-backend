package com.techlab.store.dto;

import java.util.List;

public record OrderResponse(
        String orderId,
        List<OrderItemDto> failed
) { }

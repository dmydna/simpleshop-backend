package com.techlab.store.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotNull;

public record BuyRequest(
        @NotNull(message = "Order ID is required")
        Long orderId,
        @NotNull(message = "Quantity is required")
        Integer quantity,
        @NotNull(message = "Total amount is required")
        BigDecimal totalAmount,
        @NotNull(message = "Payment token is required")
        List<OrderItemDto>failedItems,
        String paymentToken // ← Token generado por Stripe/PayPal
) {}
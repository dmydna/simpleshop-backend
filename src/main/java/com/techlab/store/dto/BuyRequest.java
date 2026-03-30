package com.techlab.store.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public record BuyRequest(
        @NotNull(message = "Order ID is required")
        Long orderId,
        @NotNull(message = "Quantity is required")
        Integer quantity,
        @NotNull(message = "Total amount is required")
        BigDecimal totalAmount,
        @NotNull(message = "Payment token is required")
        Set<OrderDetailDTO>failedDetails,
        String paymentToken // ← Token generado por Stripe/PayPal
) {}
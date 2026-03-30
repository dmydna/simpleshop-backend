package com.techlab.store.utils;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record BuyRequest(
        @NotNull(message = "Product ID is required")
        List <Long> cartProductIDs,
        Long listingId,
        Long productId,
        Double price,
        @NotNull(message = "Quantity is required")
        Integer quantity,
        @NotNull(message = "Total amount is required")
        BigDecimal totalAmount,
        @NotNull(message = "Payment token is required")
        String paymentToken  // ← Token generado por Stripe/PayPal
) {}
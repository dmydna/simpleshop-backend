package com.techlab.store.dto;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;


public record BanRequest(
        LocalDateTime banExpiresAt,
        String banReason // ← Token generado por Stripe/PayPal
) {}
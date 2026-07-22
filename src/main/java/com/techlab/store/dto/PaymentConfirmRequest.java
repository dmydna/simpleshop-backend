package com.techlab.store.dto;

public record PaymentConfirmRequest(
        Long orderId,
        String paymentToken
) { }

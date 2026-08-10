package com.techlab.store.dto;

public record PaymentConfirmRequest(
        String orderId,
        String paymentToken
) { }

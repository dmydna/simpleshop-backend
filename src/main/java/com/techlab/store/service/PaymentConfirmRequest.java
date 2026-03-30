package com.techlab.store.service;

public record PaymentConfirmRequest(
        Long orderId,
        String paymentToken
) { }

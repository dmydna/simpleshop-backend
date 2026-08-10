package com.techlab.store.dto;
import lombok.*;


public record PaymentRequest(
    String orderId,
    String userEmail
){}

package com.techlab.store.dto;
import lombok.*;


public record PaymentRequest(
    Long orderId,
    String userEmail
){}

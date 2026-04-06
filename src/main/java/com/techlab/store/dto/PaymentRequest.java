package com.techlab.store.dto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private Long orderId;
    private String userEmail;
}


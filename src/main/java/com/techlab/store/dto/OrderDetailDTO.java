package com.techlab.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    private Long id;
    private Long productId;
    private Long listingId;
    private String name;
    private int stock;
    private int quantity;
    private double priceAtPurchase; // precio a pagar
}

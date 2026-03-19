package com.techlab.store.dto;

import com.techlab.store.entity.Order;
import com.techlab.store.entity.Product;
import lombok.*;

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

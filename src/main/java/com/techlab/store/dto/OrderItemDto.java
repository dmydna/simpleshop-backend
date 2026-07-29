package com.techlab.store.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemDto (
    Long id,
    Long orderId,
    Long listingId,
    Long productId,
    Long reviewId,
    Double rating,
    String thumbnail,
    String name,
    String status,
    int stock,
    int quantity,
    BigDecimal priceAtPurchase, // precio a pagar
    Integer discountPercentageAtPurchase,
    LocalDateTime createdAt
){}

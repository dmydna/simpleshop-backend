package com.techlab.store.dto;

import java.math.BigDecimal;

public record DiscountResult(
    BigDecimal basePrice, 
    Integer percent, 
    BigDecimal discountAmount, 
    BigDecimal finalPrice
) {}
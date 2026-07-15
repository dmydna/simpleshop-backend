package com.techlab.store.dto;

import java.math.BigDecimal;

public record ReviewRequest(
    Long id,
    String hash,
    String title,
    BigDecimal price,
    String thumbnail
) {}

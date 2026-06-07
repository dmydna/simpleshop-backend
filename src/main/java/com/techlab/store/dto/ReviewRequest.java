package com.techlab.store.dto;

public record ReviewRequest(
    Long id,
    String hash,
    String title,
    Double price,
    String thumbnail
) {}

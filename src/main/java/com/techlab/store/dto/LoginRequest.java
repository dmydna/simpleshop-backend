package com.techlab.store.dto;

/**
 * Los records son inmutables y generan automáticamente
 * los getters, constructor, equals y hashCode.
 */
public record LoginRequest(
        String username,
        String password
) {}
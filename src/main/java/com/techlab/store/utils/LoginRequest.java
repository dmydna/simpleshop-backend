package com.techlab.store.utils;

/**
 * Los records son inmutables y generan automáticamente
 * los getters, constructor, equals y hashCode.
 */
public record LoginRequest(
        String username,
        String password
) {}
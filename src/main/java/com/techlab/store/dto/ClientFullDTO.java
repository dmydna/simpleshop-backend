package com.techlab.store.dto;
import java.util.List;


public record ClientFullDTO (
    Long id,
    // user
    String username,
    String email,
    // client
    String firstName,
    String lastName,
    String address,
    String phone,
    List<OrderSummary> orders
){}

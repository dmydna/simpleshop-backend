package com.techlab.store.dto;


public record ClientDTO(
    // client
    Long id,
    String firstName,
    String lastName,
    String address,
    String phone
){}

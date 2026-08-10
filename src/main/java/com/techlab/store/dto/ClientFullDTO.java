package com.techlab.store.dto;
import java.util.List;


public record ClientFullDTO (
    String id,
    // user
    String username,
    String email,
    // client
    String firstName,
    String lastName,
    String address,
    String phone
){}

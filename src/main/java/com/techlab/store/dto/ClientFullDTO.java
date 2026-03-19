package com.techlab.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;


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
    Boolean deleted,
    Set<OrderSimpleDTO> orders
){}

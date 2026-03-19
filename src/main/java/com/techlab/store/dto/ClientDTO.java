package com.techlab.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


public record ClientDTO(
    // client
    Long id,
    String firstName,
    String lastName,
    String address,
    String phone,
    Boolean deleted
){}

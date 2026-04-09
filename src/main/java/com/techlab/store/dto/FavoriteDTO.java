package com.techlab.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


public record FavoriteDTO(
    // client
    Long id,
    Long listingId
){}

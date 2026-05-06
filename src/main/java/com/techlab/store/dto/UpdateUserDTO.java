package com.techlab.store.dto;

public record UpdateUserDTO(
//    no se cambia username?
//        String username,   
        String password,
        String email,
        String image
) {}
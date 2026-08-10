package com.techlab.store.config; 

import org.hashids.Hashids;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HashidsConfig {

    private static final String SALT = "mi-clave-secreta-super-segura-2026";
    private static final int MIN_HASH_LENGTH = 8; 

    @Bean
    public Hashids hashids() {
        return new Hashids(SALT, MIN_HASH_LENGTH);
    }
}
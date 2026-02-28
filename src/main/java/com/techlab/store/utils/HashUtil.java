package com.techlab.store.utils;

import java.util.UUID;

public class HashUtil {
    public static String generateShortHash() {
        // Genera un UUID y toma los primeros 8 caracteres
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}

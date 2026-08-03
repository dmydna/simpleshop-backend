package com.techlab.store.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class HashUtil {

    private static final SecureRandom random = new SecureRandom();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    private static final String ALPHA_NUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMERIC_ONLY = "0123456789";


    public static String generateShortHash() {
        // Genera un UUID y toma los primeros 8 caracteres
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public static String generateNumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(NUMERIC_ONLY.length());
            sb.append(NUMERIC_ONLY.charAt(index));
        }
        return sb.toString();
    }

    public static String generateOperationNumber(){
        String datePart = LocalDateTime.now().format(DATE_FORMATTER);
        return datePart + generateNumeric(16 - datePart.length());
    }
}

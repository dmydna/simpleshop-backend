package com.techlab.store.security;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    public ResponseCookie createAccessTokenCookie(String token, long durationInSeconds) {
        return ResponseCookie.from("accessToken", token)
                .httpOnly(true)       // Protege contra XSS
                .secure(true)         // Requiere HTTPS
                .path("/")            // Disponible para toda la API
                .maxAge(durationInSeconds)
                .sameSite("Strict")   // Protege contra CSRF
                .build();
    }

    public ResponseCookie createCleanCookie() {
        return ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)            // Expira inmediatamente la cookie
                .build();
    }
}
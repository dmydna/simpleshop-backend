package com.techlab.store.security;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import static com.techlab.store.security.SecurityConstants.*;



@Component
public class CookieUtils {

    public ResponseCookie createAccessTokenCookie(String token, long durationInSeconds) {
        return ResponseCookie.from(COOKIE_ACCESS_TOKEN_NAME, token)
                .httpOnly(true)       // Protege contra XSS
                .secure(true)         // Requiere HTTPS
                .path(COOKIE_PATH)            // Disponible para toda la API
                .maxAge(durationInSeconds)
                .sameSite("Strict")   // Protege contra CSRF
                .build();
    }

    public ResponseCookie createCleanCookie() {
        return ResponseCookie.from(COOKIE_ACCESS_TOKEN_NAME, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)            // Expira inmediatamente la cookie
                .build();
    }
}
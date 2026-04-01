package com.techlab.store.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class PaymentGatewayService {

    private static final String SECRET_KEY = "tu_clave_secreta_32_caracteres_12345678";

    public String generateToken(String orderId, String clientId) {
        try {
            return Jwts.builder()
                    .setSubject(orderId)
                    .claim("clientId", clientId)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10 min
                    .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Error generando token", e);
        }
    }

    public String validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token);

            String orderId = claims.getBody().getSubject();
            String clientId = claims.getBody().get("clientId", String.class);
            return "Token válido: Order=" + orderId + ", Client=" + clientId;
        } catch (Exception e) {
            throw new RuntimeException("Token inválido o expirado", e);
        }
    }
}
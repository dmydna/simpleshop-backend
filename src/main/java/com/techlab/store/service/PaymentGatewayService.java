package com.techlab.store.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class PaymentGatewayService {

    private static final String SECRET_KEY = "tu_clave_secreta_32_caracteres_12345678";

    public String generateToken(Long orderId, String userEmail) {
        log.info("🔔 Generando token de compra para la order con id {} ...", orderId);
        try {
            return Jwts.builder()
                    .setSubject(String.valueOf(orderId))
                    .claim("userEmail", userEmail)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10 min
                    .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Error generando token", e);
        }
    }

    public Boolean validateToken(String token) {
        log.info("🔔 Validando token de compra ...");
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token);

            String orderId = claims.getBody().getSubject();
            String userEmail = claims.getBody().get("userEmail", String.class);

            log.info("✅ Token válido para order_id: {}, user_mail: {} ...", orderId, userEmail);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Token inválido o expirado", e);
        }
    }
}
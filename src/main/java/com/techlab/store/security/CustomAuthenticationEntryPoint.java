package com.techlab.store.security;

import java.io.IOException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String message = "No autenticado. Se requiere inicio de sesion.";
        String code = "UNAUTHORIZED";

        // Revisamos si el filtro guardó un problema específico con el JWT
        Object jwtException = request.getAttribute("jwt_exception");

        if (jwtException instanceof ExpiredJwtException) {
            message = "La sesion ha expirado. Por favor, vuelve a iniciar sesion.";
            code = "TOKEN_EXPIRED";
        } else if (jwtException instanceof JwtException) {
            message = "El token de autenticación es inválido.";
            code = "INVALID_TOKEN";
        }

        response.getWriter().write(
                String.format("{\"error\": \"%s\", \"code\": \"%s\"}", message, code));
    }
}

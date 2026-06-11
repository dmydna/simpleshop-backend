package com.techlab.store.service;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        System.out.println(">>> CustomAuthenticationEntryPoint llamado con mensaje: " + authException.getMessage());


        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String message = "Token expirado o invalido";
        String code = "AUTH_ERROR";

        // Si la excepción es de tipo Expirado (requiere import de io.jsonwebtoken)
        if (authException.getCause() instanceof ExpiredJwtException) {
            message = "Token expirado";
            code = "TOKEN_EXPIRED";
        } else if (authException.getMessage() != null && authException.getMessage().contains("invalid")) {
            message = "Token inválido";
            code = "INVALID_TOKEN";
        }

        response.getWriter().write("{\"error\": \"" + message + "\", \"code\": \"" + code + "\"}");
    }
}
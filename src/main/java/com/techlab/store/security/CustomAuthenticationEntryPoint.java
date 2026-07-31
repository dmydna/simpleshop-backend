package com.techlab.store.security;

import java.io.IOException;
import io.jsonwebtoken.ExpiredJwtException;

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
        AuthenticationException authException
    ) throws IOException {

        log.info("CustomAuthenticationEntryPoint llamado con mensaje: " + authException.getMessage());

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
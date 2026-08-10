package com.techlab.store.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.techlab.store.utils.StringUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. Intentar obtener el token de la Cookie
        String token = extractTokenFromCookie(request);

        // 2. Si no vino en cookie, buscar en el Header (soporte v1/v2)
        if (token == null) {
            token = extractTokenFromHeader(request);
        }

        // 3. Validar y establecer la autenticación en Spring Security
        if (token != null) {
            try {
                // Si el token es válido, creamos el contexto de seguridad
                if (jwtService.validateToken(token)) {
                    UsernamePasswordAuthenticationToken auth 
                    = (UsernamePasswordAuthenticationToken) jwtService.getAuthentication(token);

                    Date expiration = jwtService.extractExpiration(token);
                    Long expirationMillis = expiration.getTime();

                    auth.setDetails(expirationMillis);

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (ExpiredJwtException e) {
                // El token expiró: guardamos la excepción específica
                request.setAttribute("jwt_exception", e);
            } catch (JwtException | IllegalArgumentException e) {
                // El token es inválido, firma errónea o malformado
                request.setAttribute("jwt_exception", e);
            }
        }

        // 4. Continuar con el resto de la cadena de filtros
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null)
            return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "accessToken".equals(cookie.getName())) // Nombre de tu cookie
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
package com.techlab.store.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // IMPORTANTE: Paquete 'authentication'
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.AuthenticationEntryPoint;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPoint customAuthenticationEntryPoint;

    // HACK: Retorno directo tras llamar a customError.commence().
    // Spring Security no gestiona bien la BadCredentialsException en este contexto (la ignora o la convierte en 403).
    // Escribir la respuesta manualmente y retornar asegura que el 401 y el mensaje de "Token expirado" lleguen al frontend.
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        log.info("Entrando al filtro para la ruta: {}", request.getRequestURI());

        // 1. Si no hay token, pasamos al siguiente filtro
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Sin token de autorización");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        
        // 2. Intentamos extraer el nombre de usuario, capturando la excepción de expiración AQUÍ
        try {
            username = jwtService.extractUsername(jwt);
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado detectado al extraer username: {}", e.getMessage());
            // Lanzamos la excepción de autenticación para que Spring Security la maneje
            // throw new BadCredentialsException("Token expirado");
            customAuthenticationEntryPoint.commence(request, response, new BadCredentialsException("Token expirado", e));
            return; // Detener ejecución
        } catch (Exception e) {
            log.error("Error al extraer username del token: {}", e.getMessage());
            // throw new BadCredentialsException("Token inválido o malformado");
            customAuthenticationEntryPoint.commence(request, response, new BadCredentialsException("Token invalido o malformado", e));
            return;
        }

        // 3. Si hay usuario y no está autenticado
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Token válido: Autenticar
                    List<String> roles = jwtService.extractRoles(jwt);
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.info("Usuario autenticado exitosamente: {}", username);
                } else {
                    // Token inválido (devolvió false, pero no lanzó excepción)
                    log.warn("Token inválido o expirado para usuario: {}", username);
                    // throw new BadCredentialsException("Token expirado o invalido");
                    customAuthenticationEntryPoint.commence(request, response, new BadCredentialsException("Token expirado o invalido"));
                    return;              }

            } catch (Exception e) {
                log.error("Error procesando token o usuario: {}", e.getMessage(), e);
                if (e instanceof AuthenticationException) {
                    throw e;
                }
                // throw new BadCredentialsException("Error interno al validar token", e);
                customAuthenticationEntryPoint.commence(request, response, new BadCredentialsException("Error interno al validar token",e));
            }
        }

        filterChain.doFilter(request, response);
    }

}
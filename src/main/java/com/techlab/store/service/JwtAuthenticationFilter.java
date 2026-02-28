package com.techlab.store.service;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException, java.io.IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        System.out.println("Entrando al filtro para la ruta: " + request.getRequestURI());

        // 1. Si no hay token, seguimos con el siguiente filtro
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No hay token");
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraemos el token (después de la palabra "Bearer ")
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt); // Necesitarás este método en tu JwtService

        // 3. Si hay usuario y no está ya autenticado en esta petición
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {


                // 1. Extraemos los roles directamente del JWT
                List<String> roles = jwtService.extractRoles(jwt);

                System.out.println("Roles extraídos del token: " + roles);

                // 2. Creamos las autoridades usando el texto EXACTO del token
                // Usamos SimpleGrantedAuthority SIN añadir "ROLE_" manualmente
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                // 3. Pasamos las autoridades extraídas del TOKEN, no del userDetails
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        System.out.println("Filtro JWT finalizado para: " + username);
        filterChain.doFilter(request, response);
    }
}

package com.techlab.store.security;


import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import static com.techlab.store.security.SecurityConstants.*;


import com.techlab.store.dto.LoginRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;


import java.security.Key;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor 
public class JwtTokenProvider {
    // Esta es tu "firma". En producción debe ser secreta y larga.

    private final UserDetailsService userDetailsService;
    private static final String SECRET_KEY = "tu_clave_secreta_super_larga_y_segura_para_el_backend_de_techlab";

    // Añade este método o modifica el existente
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        // Agregamos los roles al token
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(extraClaims) // <--- Aquí metemos los roles
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + JWT_EXPIRATION_IN_MS))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }




    public String generateToken(Authentication authentication) {
        Map<String, Object> extraClaims = new HashMap<>();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Agregamos los roles al token
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(extraClaims) // <--- Aquí metemos los roles
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + JWT_EXPIRATION_IN_MS))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }



    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    // Estos métodos usan la librería JJWT para leer el contenido del token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return (List<String>) claims.get("roles");
    }


    public Authentication getAuthentication(String token) {
        // 1. Extraer el username del token
        String username = extractUsername(token);

        // 2. Cargar los detalles del usuario directamente desde la BD
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 3. Crear el objeto de autenticación con el UserDetails completo
        return new UsernamePasswordAuthenticationToken(
                userDetails, 
                null, 
                userDetails.getAuthorities()
        );
    }


    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public boolean validateToken(String token) {        
        String username = extractUsername(token);
        // 2. Cargar los detalles del usuario directamente desde la BD
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);

    }


    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


}
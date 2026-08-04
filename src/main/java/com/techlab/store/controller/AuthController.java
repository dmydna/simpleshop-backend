package com.techlab.store.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.techlab.store.security.SecurityConstants.*;

import com.techlab.store.entity.User;
import com.techlab.store.security.CookieUtils;
import com.techlab.store.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;

import com.techlab.store.dto.AuthResponse;
import com.techlab.store.dto.LoginRequest;
import com.techlab.store.dto.RegisterRequest;
import com.techlab.store.dto.PasswordChangeRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @Autowired
    private CookieUtils cookieUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        String token = authResponse.accessToken();
        ResponseCookie cookie = cookieUtils.createAccessTokenCookie(token, COOKIE_EXPIRATION_IN_MS);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User newUser = authService.register(request);
        return ResponseEntity.ok("Usuario registrado exitosamente con ID: " + newUser.getId());
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            Authentication authentication, 
            @RequestBody PasswordChangeRequest request) {
    
        String username = authentication.getName(); 
        authService.changePasswordByUsername(username, request);
    
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cleanCookie = cookieUtils.createCleanCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, cleanCookie.toString());
        return ResponseEntity.ok().build();
    }

}

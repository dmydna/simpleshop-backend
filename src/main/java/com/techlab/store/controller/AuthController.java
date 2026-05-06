package com.techlab.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.store.entity.User;
import com.techlab.store.service.AuthService;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request) ;
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User newUser = authService.register(request);
            return ResponseEntity.ok("Usuario registrado exitosamente con ID: " + newUser.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String tokenHeader, @RequestBody PasswordChangeRequest request) {
            // Extrae el token del header (ej: "Bearer <token>")
            authService.changePassword(tokenHeader.substring(7), request);
            return ResponseEntity.ok("Contraseña actualizada correctamente");
   } 


}

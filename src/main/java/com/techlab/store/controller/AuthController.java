package com.techlab.store.controller;

import static com.techlab.store.security.SecurityConstants.COOKIE_EXPIRATION_IN_MS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.store.dto.AuthResponse;
import com.techlab.store.dto.EmailChangeRequest;
import com.techlab.store.dto.LoginRequest;
import com.techlab.store.dto.PasswordChangeRequest;
import com.techlab.store.dto.RegisterRequest;
import com.techlab.store.dto.UserResponse;
import com.techlab.store.security.CookieUtils;
import com.techlab.store.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
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


    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("CLIENT");


        Long  expiresAtMillis = null;
        if (authentication.getDetails() instanceof Long millis) {
             expiresAtMillis = millis;
        }
        // Instant expiresAt = null;
        // if (authentication.getDetails() instanceof Date date) {
        //    expiresAt = date.getTime();
        // }

        return ResponseEntity.ok(new UserResponse(username, role, expiresAtMillis));
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            Authentication authentication, 
            @RequestBody PasswordChangeRequest request) {
    
        String username = authentication.getName(); 
        authService.changePasswordByUsername(username, request);
    
        return ResponseEntity.ok().build();
    }


    @PostMapping("/change-email")
    public ResponseEntity<?> changeEmail(
            Authentication authentication, 
            @RequestBody EmailChangeRequest request) {
    
        String username = authentication.getName(); 
        authService.changeUserEmail(username, request);
    
        return ResponseEntity.ok().build();
    }



    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cleanCookie = cookieUtils.createCleanCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, cleanCookie.toString());
        return ResponseEntity.ok().build();
    }

}

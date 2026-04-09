package com.techlab.store.service;

import com.techlab.store.utils.AuthResponse;
import com.techlab.store.utils.LoginRequest;
import com.techlab.store.entity.User;
import com.techlab.store.utils.RegisterRequest;
import com.techlab.store.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final ClientService clientService;

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("CLIENT");

        // 5. Devolver el objeto estructurado
        return new AuthResponse(
                token,
                "Bearer",
                userDetails.getUsername(),
                role
        );
    }

    @Transactional
    public User register(RegisterRequest request) {
        // Registra user en el userService
        User savedUser = userService.create(request);
        // Registra client y relaciona con usuario en clientService
        clientService.create(request, savedUser);
        return savedUser;
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public boolean isRole(String role) {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        // Normalizamos el rol: si nos pasan "ADMIN", lo convertimos en "ROLE_ADMIN"
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(roleWithPrefix));
    }

    public boolean isAdmin() {
        return isRole("ADMIN");
    }

    public User getUser(){
        Authentication auth = getAuthentication();
        return userService.findEntityByUsername(auth.getName());
    }

    @Transactional
    public List<User> saveAll(List<RegisterRequest> listRequests) {
        List<User> savedUsers = new ArrayList<>();
        for (RegisterRequest request : listRequests) {
            User savedUser = userService.create(request);
            clientService.create(request, savedUser);
            savedUsers.add(savedUser);
        }
        return savedUsers;
    }

}
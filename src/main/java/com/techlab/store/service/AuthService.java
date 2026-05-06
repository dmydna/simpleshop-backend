package com.techlab.store.service;

import java.util.ArrayList;
import java.util.List;

import com.techlab.store.dto.PasswordChangeRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techlab.store.dto.AuthResponse;
import com.techlab.store.dto.LoginRequest;
import com.techlab.store.dto.RegisterRequest;
import com.techlab.store.entity.User;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final ClientService clientService;


    // TODO impedir acceso a users con status BANNED o DELETED
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

    @Transactional
    public void changePassword(String token, PasswordChangeRequest request) {
        // 1. Extraer el username del token (Seguridad)
        String username = jwtService.extractUsername(token);
        // 2. Delegar la lógica de negocio al UserService
        // El UserService se encargará de buscar en BD, validar hashes y guardar
        userService.changePassword(username, request.oldPassword(), request.newPassword());
        
        // 3. (Opcional) Invalidar tokens anteriores si tu sistema lo requiere
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
        return userService.findByUsername(auth.getName());
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

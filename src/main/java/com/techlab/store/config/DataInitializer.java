package com.techlab.store.config;

import com.techlab.store.repository.UserRepository;
import com.techlab.store.repository.ClientRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.techlab.store.dto.RegisterRequest;
import com.techlab.store.entity.User;
import com.techlab.store.service.UserService;
import com.techlab.store.entity.Client;
import com.techlab.store.enums.Role;
import com.techlab.store.service.AuthService;

@Component
@RequiredArgsConstructor // <--- Genera el constructor automáticamente
public class DataInitializer implements CommandLineRunner {

    //CHECKME: se crea cliente para el admin.

    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String username;

    @Value("${app.admin.password}")
    private String password;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername(username).isEmpty()) {

            RegisterRequest admin_request = new RegisterRequest(
                username, 
                password, 
                baseUrl + "/uploads/users/admin.png", 
                "admin@mail.com", 
                "jhon", 
                "doe", 
                "1111-5555",
                "st 999"
            );

            // HACK asignar role a ADMIN
            authService.register(admin_request);
            User user = userService.findByUsername(username);
            user.setRole(Role.ADMIN);
            userService.updateRole(user);

            System.out.println("🔑 Admin user y su perfil Client creados correctamente.");
        }
    }
}

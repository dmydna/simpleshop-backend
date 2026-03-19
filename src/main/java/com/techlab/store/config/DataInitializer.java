package com.techlab.store.config;


import com.techlab.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.techlab.store.entity.User;
import com.techlab.store.enums.Role;

@Component
@RequiredArgsConstructor // <--- Genera el constructor automáticamente
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin1234")); // Cambia esto después
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println(">> Admin user created successfully.");
        }
    }
}
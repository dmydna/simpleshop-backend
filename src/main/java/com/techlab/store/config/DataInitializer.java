package com.techlab.store.config;

import com.techlab.store.repository.UserRepository;
import com.techlab.store.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.techlab.store.entity.User;
import com.techlab.store.entity.Client;
import com.techlab.store.enums.Role;

@Component
@RequiredArgsConstructor // <--- Genera el constructor automáticamente
public class DataInitializer implements CommandLineRunner {

    // TODO chequiar que el FIX crea un cliente para el admin.

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String username;

    @Value("${app.admin.password}")
    private String password;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User admin = new User();
            admin.setUsername(username);
            admin.setEmail("admin@mail.com");
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);
            User savedUser = userRepository.save(admin);

            Client client = new Client();
            client.setLastName("@lastname");
            client.setFirstName("@name");
            client.setUser(savedUser); // La relación se establece aquí

            clientRepository.save(client);

            System.out.println("🔑 Admin user y su perfil Client creados correctamente.");
        }
    }
}

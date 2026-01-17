package com.techlab.store.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FileStorageService {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private final Path root = Paths.get("/app/uploads/");

    public String storeFile(MultipartFile file, Long id) {
        try {
            // Crear carpeta si no existe
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            String fileName = "prod_" + id + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.root.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            
            return baseUrl + "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo: " + e.getMessage());
        }
    }
}
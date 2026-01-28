package com.techlab.store.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
            // Añadimos UUID para evitar que archivos con el mismo nombre se sobrescriban
            String uniqueID = UUID.randomUUID().toString().substring(0, 8);
            String fileName = "prod_" + id + "_" + uniqueID + "_" + file.getOriginalFilename();
            
            Files.copy(file.getInputStream(), this.root.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            
            return baseUrl + "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar archivo: " + e.getMessage());
        }
    }


    public void deleteFile(String imageUrl) {
        try {
            // 1. Extraer solo el nombre del archivo de la URL completa
            // Ejemplo: http://localhost:8080/uploads/prod_1_abc_foto.jpg -> prod_1_abc_foto.jpg
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            // 2. Construir la ruta completa al archivo
            Path filePath = this.root.resolve(fileName);
            // 3. Borrar el archivo si existe
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo eliminar el archivo físico: " + e.getMessage());
        }
    }

}
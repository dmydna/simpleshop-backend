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


    public String storeFile(MultipartFile file, Long id, String folderName) {
        try { // Define la subdiretorios
            Path targetPath = this.root.resolve(folderName);
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }

            String uniqueID = UUID.randomUUID().toString().substring(0, 8);
            String fileName = "item_" + id + "_" + uniqueID + "_" + file.getOriginalFilename();

            // Guarda en subdirectorios
            Files.copy(file.getInputStream(), targetPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            // Retorna la URL incluyendo subdirectorios
            return baseUrl + "/uploads/" + folderName + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public String storeFile(MultipartFile file, Long id) {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
            // Añade UUID para evitar que archivos con el mismo nombre se sobrescriban
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
            String marker = "/uploads/";
            if (!imageUrl.contains(marker)) return;

            // Extrae "profiles/foto.jpg" de la URL
            String relativePathStr = imageUrl.substring(imageUrl.indexOf(marker) + marker.length());

            // Resuelve la ruta física: /app/uploads/ + profiles/foto.jpg
            Path filePath = this.root.resolve(relativePathStr).normalize();

            // Seguridad: No permitir borrar fuera de /uploads/
            if (!filePath.startsWith(this.root)) {
                System.err.println("Bloqueado intento de borrado inseguro: " + filePath);
                return;
            }

            boolean deleted = Files.deleteIfExists(filePath);
            System.out.println(deleted ? "Borrado físico exitoso: " + filePath : "No se encontró archivo para borrar.");

        } catch (IOException e) {
            System.err.println("Error al eliminar archivo físico: " + e.getMessage());
            // No lanzamos excepción para no frenar el flujo si el archivo ya no estaba
        }
    }


}
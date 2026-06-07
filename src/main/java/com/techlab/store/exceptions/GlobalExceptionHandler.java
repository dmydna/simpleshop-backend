package com.techlab.store.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import com.techlab.store.exceptions.CustomExceptions.UserNotFoundException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice 
public class GlobalExceptionHandler {

    // Manejo de errores genéricos (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

        // Registra la excepción explícitamente para que aparezca en el terminal
        log.error("Error interno del servidor en la ruta desconocida {}", ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "Ocurrió un error inesperado: " + ex.getMessage());
        body.put("path", "/api/endpoint"); // Podrías pasar esto si usas WebMvcConfigurer

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Manejo de errores de negocio específicos (ej. Usuario no encontrado)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        
        // Registra la excepción explícitamente para que aparezca en el terminal
        log.error("Error Usuario no encontrado {}", ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
    
    // Manejo de errores de validación (400)
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(org.springframework.validation.BindException ex) {
        
        // Registra la excepción explícitamente para que aparezca en el terminal
        log.info("Validacion {}", ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Error de validación: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
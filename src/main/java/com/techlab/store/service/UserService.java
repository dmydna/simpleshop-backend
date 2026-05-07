package com.techlab.store.service;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.techlab.store.dto.RegisterRequest;
import com.techlab.store.dto.UserDTO;
import com.techlab.store.entity.User;
import com.techlab.store.enums.Role;
import com.techlab.store.enums.UserStatus;

import com.techlab.store.exceptions.CustomExceptions.UserNotFoundException;
import com.techlab.store.mapper.ProfileMapper;
import com.techlab.store.mapper.UserMapper;
import com.techlab.store.repository.UserRepository;
import com.techlab.store.specification.UserSpecifications;
import com.techlab.store.utils.StringUtils;
import com.techlab.store.exceptions.CustomExceptions.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor // <--- Genera el constructor automáticamente
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final StringUtils stringUtils;
    private final FileStorageService fileStorageService;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;



    public User create(User user, MultipartFile file){
        log.info("User ingresado: {}", user);
        if (!file.isEmpty()) { handleImageUpload(user, file);}
        return user;
    }


    public User create(RegisterRequest request){
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setImage(request.image());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.CLIENT); // Por defecto, todos los registros son Clientes
        User savedUser = userRepository.save(user);
        return  savedUser;
    }


    public User getById(Long id){
        User user = userRepository.findById(id)
            .orElseThrow( () -> new UserNotFoundException(id));
        return user;
    }

    public User findByUsername(String name) {
        User user =  userRepository.findByUsername(name)
                .orElseThrow(() -> new UserNotFoundException("user no encontrado"));
        return user;
    }

    private boolean isDeleted(Long id) {
        User user = getById(id);
        return user.getStatus().equals(UserStatus.DELETED);
    }

    @Transactional
    private void deleteById(Long id) {
        User user = getById(id);
        user.setStatus(UserStatus.DELETED);
    }


    @Transactional
    public User updateById(Long id, User dataToEdit) {
        User user = getById(id);
        userMapper.updateFromEntity(dataToEdit, user);
        return user;
    }


    @Transactional
    public String uploadImage(Long id, MultipartFile file) {
        User user =  getById(id);
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        return handleImageUpload(user, file);
    }


    public void handleRemoveImage(String imageUrl){
        fileStorageService.deleteFile(imageUrl);
    }

    @Transactional
    public String handleImageUpload(User user, MultipartFile file) {
       String finalUrl = fileStorageService.storeFile(file, user.getId());
       // elimina si existe imagen.
       if (!(user.getImage() == null) && !(user.getImage().isEmpty()) ) {
           handleRemoveImage(user.getImage());
       }
       user.setImage(finalUrl);
       return finalUrl;
    }



    public Page<User> filter(
            String username,
            String clientname,
            String email,
            Pageable pageable
    ) {
        // Iniciamos con una especificación base: solo usuarios no eliminados
        Specification<User> spec = Specification
            .where(UserSpecifications.isNotDeleted())
            .and(UserSpecifications.hasUsername(username))
            .and(UserSpecifications.hasEmail(email))
            .and(UserSpecifications.hasClientName(clientname));
        // findAll maneja automáticamente si la spec es null o compuesta
        return userRepository.findAll(spec, pageable);
    }


    @Transactional
    public User updateStatusById(Long id, UserStatus status){
        log.info("🔔 actualizando status de usuario con ID {}...", id);
        User user = getById(id);

        if(isDeleted(id)){ throw new UserHasDeletedException(id) ;}

        if(status.equals(UserStatus.DELETED)){ deleteById(id); }

        user.setStatus(status);
        return user;
    }



    public void changePassword(String username, String oldPassword, String newPassword) {
        // 1. Buscar usuario (Datos)
        User user = findByUsername(username);
        // 2. Validar contraseña antigua (Negocio/Seguridad)
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("Contraseña actual incorrecta");
        }
        // 3. Encriptar y guardar (Datos)
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


}

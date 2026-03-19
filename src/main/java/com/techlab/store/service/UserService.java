package com.techlab.store.service;



import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.OrderFullDTO;
import com.techlab.store.dto.UserDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.enums.Role;
import com.techlab.store.mapper.UserMapper;
import com.techlab.store.specification.ListingSpecifications;
import com.techlab.store.specification.UserSpecifications;
import com.techlab.store.utils.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import com.techlab.store.entity.User;
import com.techlab.store.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

import com.techlab.store.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor // <--- Genera el constructor automáticamente
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final StringUtils stringUtils;
    private final FileStorageService fileStorageService;
    private final UserMapper userMapper;

    public User create(User user, MultipartFile file){
        log.info("User ingresado: {}", user);
        if (!file.isEmpty()) {
            handleImageUpload(user, file);
        }
        return userRepository.save(user);
    }


    public User create(RegisterRequest request){
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.CLIENT); // Por defecto, todos los registros son Clientes
        User savedUser = userRepository.save(user);
        return  savedUser;
    }


    public UserDTO getById(Long id){
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()){
            throw new RuntimeException("User no encontrado con ID: " + id);
        }
        User entity = userOptional.get();
        return userMapper.toDto(entity);
    }


    @Transactional
    public String uploadImage(Long id, MultipartFile file) {
        User user =  userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("user no encontrado"));
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        return handleImageUpload(user, file);
    }


    public void handleRemoveImage(String imageUrl){
        fileStorageService.deleteFile(imageUrl);
    }

    public String handleImageUpload(User user, MultipartFile file) {
       String finalUrl = fileStorageService.storeFile(file, user.getId());
       // elimina si existe imagen.
       if (!(user.getImage() == null) && !(user.getImage().isEmpty()) ) {
           handleRemoveImage(user.getImage());
       }
       user.setImage(finalUrl);
       userRepository.save(user);
       return finalUrl;
    }

    public UserDTO updateById(Long id, UserDTO dataToEdit) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No encontrado"));

        if (!stringUtils.isEmpty(dataToEdit.username())){
            user.setUsername(dataToEdit.username());
        }
        if (null != dataToEdit.deleted()){
            user.setDeleted(dataToEdit.deleted());
        }
        if (!stringUtils.isEmpty(dataToEdit.image())){
            user.setImage(dataToEdit.image());
        }

        if (!stringUtils.isEmpty(dataToEdit.password())){
            user.setImage(dataToEdit.image());
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }



    public User findEntityByUsername(String name) {
        User user =  userRepository.findByUsername(name)
                .orElseThrow(() -> new RuntimeException("user no encontrado"));
        return user;
    }


    public Page<UserDTO> filter(
            String username,
            String clientname,
            String email,
            Pageable pageable
    ) {
        // Iniciamos con una especificación base: solo usuarios no eliminados
        Specification<User> spec = UserSpecifications.isNotDeleted();

        // Encadenamos dinámicamente
        if (stringUtils.hasText(username)) {
            spec = spec.and(UserSpecifications.hasUsername(username));
        }

        if (stringUtils.hasText(email)) {
            spec = spec.and(UserSpecifications.hasEmail(email));
        }

        if (stringUtils.hasText(clientname)) {
            spec = spec.and(UserSpecifications.hasClientName(clientname));
        }

        // findAll maneja automáticamente si la spec es null o compuesta
        return userRepository.findAll(spec, pageable)
                .map(userMapper::toDto); // Uso de Method Reference para más limpieza
    }

}

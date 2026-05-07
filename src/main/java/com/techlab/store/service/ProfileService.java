package com.techlab.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.techlab.store.dto.ProfileDTO;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.User;
import com.techlab.store.mapper.ClientMapper;
import com.techlab.store.mapper.ProfileMapper;
import com.techlab.store.mapper.UserMapper;
import com.techlab.store.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileMapper profileMapper;
    private final UserService userService;
    private final OrderService orderService;
    private final ClientService clientService;
    private final FileStorageService fileStorageService;
    private final ClientMapper clientMapper;
    private final UserMapper userMapper;
    private final AuthService authService;
    private final UserRepository userRepository;

    public ProfileDTO getMyProfile(Authentication authentication) {

        log.info("Principal: " + authentication.getName());
        log.info("Authorities: " + authentication.getAuthorities());

        User user = userService.findByUsername(authentication.getName());
        return profileMapper.toDto(user, user.getClient());
    }

    public Page<ProfileDTO> findByFilter(
            String username,
            String clientname,
            String email,
            Pageable pageable
    ){

        return userService
           .filter(username, clientname, email, pageable)
           .map(user -> profileMapper.toDto(user, user.getClient()));
    }


    public User getMyUser(Authentication authentication) {
        log.info("Principal: " + authentication.getName());
        log.info("Authorities: " + authentication.getAuthorities());
        return userService.findByUsername(authentication.getName());
    }

    public Client getMyClient(Authentication authentication) {
        log.info("Principal: " + authentication.getName());
        log.info("Authorities: " + authentication.getAuthorities());
        User user = userService.findByUsername(authentication.getName()); 
        return clientService.getById(user.getId());
    }

    @Transactional
    public ProfileDTO updateMyProfile(Authentication authentication, ProfileDTO dto) {

        User user = userService.findByUsername(authentication.getName());
        Client client = clientService.getById(user.getId());

        profileMapper.updateUserFromDto(user, dto);
        profileMapper.updateClientFromDto(client, dto);

        return profileMapper.toDto(user, client);
    }

    @Transactional
    public ProfileDTO updateProfile(Long id, ProfileDTO dto) {

        User user = userRepository.findById(id)
           .orElseThrow(() -> new RuntimeException("user no encontrado"));

        profileMapper.updateUserFromDto(user, dto);
        profileMapper.updateClientFromDto(user.getClient(), dto);

        return profileMapper.toDto(user, user.getClient());
    }


    public void handleRemoveImage(String imageUrl) {
        fileStorageService.deleteFile(imageUrl);
    }

    @Transactional
    public String updateProfileImage(Authentication authentication, MultipartFile file) {
        String folderName = "users";
        User user = userService.findByUsername(authentication.getName());
        String finalUrl = fileStorageService.storeFile(file, user.getId(), folderName);
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            handleRemoveImage(user.getImage());
            System.out.println("Borrando imagen antigua: " + user.getImage());
        }
        user.setImage(finalUrl);
        return finalUrl;
    }

}

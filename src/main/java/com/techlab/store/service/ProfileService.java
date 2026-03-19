package com.techlab.store.service;

import com.techlab.store.dto.ProfileDTO;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.User;
import com.techlab.store.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileMapper profileMapper;
    private final UserService userService;
    private final ClientService clientService;
    private final FileStorageService fileStorageService;

    public ProfileDTO getProfile(Authentication authentication) {

        log.info("Principal: " + authentication.getName());
        log.info("Authorities: " + authentication.getAuthorities());

        User user = userService.findEntityByUsername(authentication.getName());
        Client client = clientService.findEntityById(user.getId());

        return profileMapper.toDto(user, client);
    }

    @Transactional
    public ProfileDTO updateProfile(Authentication authentication, ProfileDTO dto) {

        User user = userService.findEntityByUsername(authentication.getName());
        Client client = clientService.findEntityById(user.getId());

        profileMapper.updateUserFromDto(user, dto);
        profileMapper.updateClientFromDto(client, dto);

        return profileMapper.toDto(user, client);
    }

    public void handleRemoveImage(String imageUrl){
        fileStorageService.deleteFile(imageUrl);
    }

    @Transactional
    public String updateProfileImage(Authentication authentication, MultipartFile file ) {
        String folderName = "users";
        User user = userService.findEntityByUsername(authentication.getName());
        String finalUrl = fileStorageService.storeFile(file, user.getId(), folderName);
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            handleRemoveImage(user.getImage());
            System.out.println("Borrando imagen antigua: " + user.getImage());
        }
        user.setImage(finalUrl);
        return finalUrl;
    }


}

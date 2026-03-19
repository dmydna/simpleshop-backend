package com.techlab.store.controller;

import com.techlab.store.dto.ProfileDTO;
import com.techlab.store.enums.Role;
import com.techlab.store.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/my")
    public ResponseEntity<ProfileDTO> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(profileService.getProfile(authentication));
    }

    @GetMapping("/rol")
    public ResponseEntity<String> getMyRol(Authentication authentication) {
        return ResponseEntity.ok(
                profileService.getProfile(authentication).role()
        );
    }


    @PutMapping("/update")
    public ResponseEntity<ProfileDTO> updateMyProfile(
            Authentication authentication,
            @RequestBody ProfileDTO dataToEdit
    ) {
        return ResponseEntity.ok(profileService.updateProfile(authentication, dataToEdit));
    }

    @PutMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMyProfileImage(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {

        return ResponseEntity.ok(profileService.updateProfileImage(authentication, file));
    }

}
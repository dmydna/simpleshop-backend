package com.techlab.store.controller;

import com.techlab.store.dto.ProfileDTO;
import com.techlab.store.enums.Role;
import com.techlab.store.service.ProfileService;
import com.techlab.store.service.AuthService;
import com.techlab.store.service.OrderService;

import com.techlab.store.dto.OrderFullDTO;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;



@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final AuthService authService;
    private final OrderService orderService;

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

   @GetMapping("/orders")
    public ResponseEntity<Page<OrderFullDTO>> getAll(
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) Order.OrderState status,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
     System.out.println("\n -- entra al filtro de ordernes [controller] -- \n");
        if (authService.isAdmin()) {
            return ResponseEntity.ok(orderService
                    .filter(userId, status, pageable));
        }
        User user = authService.getUser();
        return ResponseEntity.ok(orderService
                    .filter(user.getId(), status, pageable));

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
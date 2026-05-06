package com.techlab.store.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.techlab.store.dto.OrderComplete;
import com.techlab.store.dto.ProfileDTO;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.User;
import com.techlab.store.service.AuthService;
import com.techlab.store.service.OrderService;
import com.techlab.store.service.PendingReviewService;
import com.techlab.store.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final AuthService authService;
    private final OrderService orderService;
    private final PendingReviewService pendingReviewService;

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

    public ResponseEntity<Page<ProfileDTO>> getProfiles(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String clientname,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // El Service decide si usa filtros o si devuelve todo
        return ResponseEntity.ok(profileService.findByFilter(username, email, clientname, pageable));
    }


    //Nota: se coloca el endpoint para el usuario pueda ver sus ordenes, aunque  orders tiene un controller especifico.
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderComplete>> getMyOrders(
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

    @PutMapping("/update/{id}")
    public ResponseEntity<ProfileDTO> updateProfile(
            @PathVariable Long id,
            @RequestBody ProfileDTO dataToEdit
    ) {
        return ResponseEntity.ok(profileService
            .updateProfile(id, dataToEdit));
    }

    @PutMapping("/update")
    public ResponseEntity<ProfileDTO> updateMyProfile(
            Authentication authentication,
            @RequestBody ProfileDTO dataToEdit
    ) {
        return ResponseEntity.ok(profileService
            .updateMyProfile(authentication, dataToEdit));
    }

    @PutMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMyProfileImage(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {

        return ResponseEntity.ok(profileService.updateProfileImage(authentication, file));
    }

}

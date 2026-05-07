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
import com.techlab.store.enums.OrderStatus;
import com.techlab.store.mapper.OrderMapper;
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
    private final OrderMapper orderMapper;

    @GetMapping("/my")
    public ResponseEntity<ProfileDTO> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(profileService.getMyProfile(authentication));
    }

    @GetMapping("/rol")
    public ResponseEntity<String> getMyRol(Authentication authentication) {
        return ResponseEntity.ok(
                profileService.getMyProfile(authentication).role()
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
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Order> orders;
        if (authService.isAdmin() && userId != null) {
            orders = orderService.filter(userId, status, pageable);
        } else {
            User user = authService.getUser();
            orders = orderService.filter(user.getId(), status, pageable);
        }
        Page<OrderComplete> response = orders
            .map(order -> orderMapper.toFullDto(order));
        return ResponseEntity.ok(response);

    }

    @PutMapping("/{id}/update")
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

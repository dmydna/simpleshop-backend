package com.techlab.store.controller;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.techlab.store.dto.UserDTO;
import com.techlab.store.entity.User;
import com.techlab.store.service.BuyService;
import com.techlab.store.service.PaymentConfirmRequest;
import com.techlab.store.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buy")
public class BuyController {

    private final BuyService buyService;
    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<?> buy(
            Authentication authentication,
            @RequestBody PaymentConfirmRequest request) {
        // request contiene: { orderId, paymentToken }
        
        log.info("🔔 Se valida compra con el request {}", request);

        User user = profileService.getMyUser(authentication);
        boolean success = buyService
            .confirmPayment(
                request.orderId(), 
                request.paymentToken(), 
                user.getEmail()
            );
        if (success) return ResponseEntity.ok().build(); // 200 OK 
        return ResponseEntity.badRequest().build(); // 400 Bad Request
    }
}
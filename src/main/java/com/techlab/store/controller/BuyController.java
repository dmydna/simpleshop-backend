package com.techlab.store.controller;

import org.springframework.security.core.Authentication;


import com.techlab.store.dto.ClientDTO;
import com.techlab.store.service.BuyService;
import com.techlab.store.service.PaymentConfirmRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.techlab.store.service.ProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buy")
public class BuyController {

    private final BuyService buyService;
    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<?> buy(
            Authentication authentication,
            @Valid @RequestBody PaymentConfirmRequest request) {
        // request contiene: { orderId, paymentToken }

        ClientDTO client = profileService.getMyClient(authentication);
        boolean success = buyService.confirmPayment(
                request.orderId(),
                request.paymentToken(),
                client.id());
        return ResponseEntity.ok(success ? "Payment confirmed" : "Payment failed");
    }


}
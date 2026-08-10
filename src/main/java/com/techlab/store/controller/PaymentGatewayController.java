package com.techlab.store.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.store.dto.PaymentRequest;
import com.techlab.store.dto.TokenRequest;
import com.techlab.store.service.HashidService;
import com.techlab.store.service.PaymentGatewayService;

import lombok.RequiredArgsConstructor;


//NOTA: Este controller simula una pasarela de pago externa
@RestController
@RequiredArgsConstructor
@RequestMapping("/toy-gateway")
public class PaymentGatewayController {

    private final PaymentGatewayService paymentGatewayService;
    private final HashidService hashidService;

    @PostMapping("/initiate")
    public ResponseEntity<String> initiatePayment(@RequestBody PaymentRequest request) {
        String token = paymentGatewayService.generateToken(hashidService.decode(request.orderId()), request.userEmail());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody TokenRequest tokenRequest) {
        Boolean result = paymentGatewayService.validateToken(tokenRequest.token());
        return ResponseEntity.ok(result);
    }
}
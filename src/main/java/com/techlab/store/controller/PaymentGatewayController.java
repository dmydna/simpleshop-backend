package com.techlab.store.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.store.dto.PaymentRequest;
import com.techlab.store.dto.TokenRequest;
import com.techlab.store.service.PaymentGatewayService;

@RestController
@RequestMapping("/toy-gateway")
public class PaymentGatewayController {

    private final PaymentGatewayService paymentGatewayService;

    public PaymentGatewayController(PaymentGatewayService paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<String> initiatePayment(@RequestBody PaymentRequest request) {
        try {
            String token = paymentGatewayService.generateToken(request.orderId(), request.userEmail());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody TokenRequest tokenRequest) {
        try {
            Boolean result = paymentGatewayService.validateToken(tokenRequest.token());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Error: " + e.getMessage());
        }
    }
}
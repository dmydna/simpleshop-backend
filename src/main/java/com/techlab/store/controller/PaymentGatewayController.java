package com.techlab.store.controller;


import lombok.RequiredArgsConstructor;
import com.techlab.store.dto.PaymentRequest;
import com.techlab.store.dto.TokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            String token = paymentGatewayService.generateToken(request.getOrderId(), request.getClientId());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestBody TokenRequest tokenRequest) {
        try {
            String result = paymentGatewayService.validateToken(tokenRequest.getToken());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Error: " + e.getMessage());
        }
    }
}
package com.techlab.store.controller;

import com.techlab.store.dto.BuyRequest;
import com.techlab.store.dto.OrderDetailDTO;
import com.techlab.store.dto.OrderFullDTO;
import com.techlab.store.service.BuyService;
import com.techlab.store.service.OrderService;
import com.techlab.store.service.PaymentConfirmRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buy")
public class BuyController {

    @Autowired
    private final BuyService buyService;
    private final OrderService orderService;



    @PostMapping
    public ResponseEntity<?> buy(
            @Valid @RequestBody PaymentConfirmRequest request,
            @RequestParam Long clientId) {
        // request contiene: { orderId, paymentToken }
        boolean success = buyService.confirmPayment(
                request.orderId(),
                request.paymentToken(),
                clientId);
        return ResponseEntity.ok(success ? "Payment confirmed" : "Payment failed");
    }


}
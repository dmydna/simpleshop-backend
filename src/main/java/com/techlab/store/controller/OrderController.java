package com.techlab.store.controller;

import com.techlab.store.dto.BuyRequest;
import com.techlab.store.dto.OrderFullDTO;
import com.techlab.store.dto.ClientDTO;
import com.techlab.store.dto.OrderResponse;
import com.techlab.store.entity.Order;
import com.techlab.store.service.BuyService;
import com.techlab.store.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

import com.techlab.store.service.AuthService;
import com.techlab.store.service.ClientService;
import com.techlab.store.service.ProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")// Endpoint base
public class OrderController {

    @Autowired
    private final OrderService orderService;
    private final BuyService buyService;
    private final ProfileService profileService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<?> createOrder(
            Authentication authentication,
            @RequestParam(required = false) Long clientId,
            @RequestBody OrderFullDTO dto
            ) {
        Long clientID; 

        if(authService.isAdmin() && clientId != null){
            clientID = clientId; 
        }
        else if(authService.isAdmin() && clientId == null){
            throw new IllegalArgumentException("Admin debe proporcionar clientId");
        }else{
            ClientDTO client = profileService.getMyClient(authentication);
            clientID = client.id();
        }
        OrderFullDTO savedOrder = buyService.SavedOrder(dto, clientID);
        return ResponseEntity.ok(new OrderResponse(
                savedOrder.getId(),
                savedOrder.getFailedDetails()
        ));
    }



    @GetMapping("/{id}")
    public OrderFullDTO getOrderById(@PathVariable Long id) {
        return this.orderService.getById(id);
    }

    @GetMapping("/client/{id}")
    public  List<OrderFullDTO> getOrderByClientId(@PathVariable Long id){
        return this.orderService.getOrderByClientId(id);
    }

    @GetMapping
    public List<OrderFullDTO> getAllOrders() {
        return this.orderService.getAll();
    }


    @PutMapping("/{id}/status")
    public OrderFullDTO updateStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderState newState) {
        return this.orderService.updateStatus(id, newState);
    }
}

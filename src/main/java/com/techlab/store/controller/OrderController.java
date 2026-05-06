package com.techlab.store.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.techlab.store.enums.OrderStatus;
import com.techlab.store.dto.ClientDTO;
import com.techlab.store.dto.OrderComplete;
import com.techlab.store.dto.OrderResponse;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.User;
import com.techlab.store.mapper.OrderMapper;
import com.techlab.store.service.AuthService;
import com.techlab.store.service.BuyService;
import com.techlab.store.service.OrderService;
import com.techlab.store.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")// Endpoint base
public class OrderController {

    private final OrderService orderService;
    private final BuyService buyService;
    private final ProfileService profileService;
    private final AuthService authService;
    private final OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<?> createOrder(
            Authentication authentication,
            @RequestParam(required = false) Long clientId,
            @RequestBody OrderComplete dto
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
        OrderComplete savedOrder = buyService.savedOrder(dto, clientID);
        return ResponseEntity.ok(new OrderResponse(
                savedOrder.id(),
                savedOrder.failedItems()
        ));
    }


    @GetMapping
    public ResponseEntity<Page<OrderComplete>> getAll(
        @RequestParam(required = false) Long userId, // userId = clientId
        @RequestParam(required = false) OrderStatus status,
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


    @GetMapping("/{id}")
    public ResponseEntity<OrderComplete> getOrderById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        OrderComplete response = orderMapper.toFullDto(order);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelById(@PathVariable Long id) {
        boolean success = this.orderService.cancelOrderById(id);
        if (success) return ResponseEntity.ok().build(); // 200 OK 
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}/status")
    public OrderComplete updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus newStatus) {
        return this.orderService.updateStatus(id, newStatus);
    }
}

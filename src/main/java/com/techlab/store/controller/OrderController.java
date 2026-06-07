package com.techlab.store.controller;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

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
import com.techlab.store.dto.CreateOrderDTO;
import com.techlab.store.dto.OrderComplete;
import com.techlab.store.dto.OrderResponse;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.User;
import com.techlab.store.mapper.ClientMapper;
import com.techlab.store.mapper.OrderMapper;
import com.techlab.store.service.AuthService;
import com.techlab.store.service.BuyService;
import com.techlab.store.service.ClientService;
import com.techlab.store.service.OrderService;
import com.techlab.store.service.ProfileService;
import lombok.extern.slf4j.Slf4j;

import lombok.RequiredArgsConstructor;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")// Endpoint base
public class OrderController {

    private final OrderService orderService;
    private final BuyService buyService;
    private final ProfileService profileService;
    private final AuthService authService;
    private final OrderMapper orderMapper;
    private final ClientMapper clientMapper;
    private final ClientService clientService;

    // CHECKME: cambio input a CreateOrderDTO
    @PostMapping
    public ResponseEntity<?> createOrder(
            Authentication authentication,
            @RequestParam(required = false) Long clientId,
            @RequestBody CreateOrderDTO dto
            ) {
        Client client;
        if(authService.isAdmin() && clientId != null){
            client = clientService.getById(clientId);
        }else{
            client = profileService.getMyClient(authentication);
        } 
        Order entity = orderMapper.toEntity(dto);
        entity.setClient(client); // Importante establecer relacion client/order
        Order savedOrder = orderService.createOrder(entity);
        return ResponseEntity.ok(new OrderResponse(
                savedOrder.getId(),
                orderMapper.toItemDtoList(savedOrder.getFailedItems())
        ));
    }


    @GetMapping
    public ResponseEntity<Page<OrderComplete>> getAll(
        @RequestParam(required = false) Long userId, // userId = clientId
        @RequestParam(required = false) OrderStatus status,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Order> filtered;
        User user = authService.getUser();
        if (authService.isAdmin()) {
            // Si admin no envia una id se usa la propia.
            long id = userId != null ? userId : user.getId();
            log.info("🔔 ADMIN obtiene ordenes de id...",id);
            filtered = orderService.filter(user.getId(), status, pageable);
        }else{
            filtered = orderService.filter(user.getId(), status, pageable);
        }
        return ResponseEntity.ok(filtered.map(order -> orderMapper.toFullDto(order)));
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
        Order entity = orderService.updateStatus(id, newStatus);
        return orderMapper.toFullDto(entity);
    }
}

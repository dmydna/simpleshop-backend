package com.techlab.store.controller;

import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.techlab.store.dto.UserDTO;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.OrderItem;
import com.techlab.store.entity.User;
import com.techlab.store.enums.OrderStatus;
import com.techlab.store.enums.Role;
import com.techlab.store.mapper.OrderMapper;
import com.techlab.store.repository.OrderItemRepository;
import com.techlab.store.service.BuyService;
import com.techlab.store.dto.OrderItemDto;
import com.techlab.store.dto.PaymentConfirmRequest;
import com.techlab.store.service.ProfileService;
import com.techlab.store.specification.OrderSpecifications;
import org.springframework.data.domain.Sort;

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
    private final OrderMapper orderMapper; 


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


    @GetMapping("/history")
    public ResponseEntity<Page<OrderItemDto>> getOrderItems(
        Authentication authentication,
        @RequestParam(required = false) Long userId, // userId = clientId
        @RequestParam(required = false, defaultValue = "PAID") OrderStatus status,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<OrderItem> filtered;
        User user = profileService.getMyUser(authentication);
        if (user.getRole().equals(Role.ADMIN) ) {
            // Si admin no envia una id se usa la propia.
            long id = userId != null ? userId : user.getId();
            log.info("🔔 ADMIN obtiene compras de userId: {}...",id);
            filtered = buyService.filter(user.getId(), status, pageable);
        }else{
            filtered = buyService.filter(user.getId(), OrderStatus.PAID, pageable);
        }
        return ResponseEntity.ok(filtered.map(item -> orderMapper.toItemDto(item)));
    }



}
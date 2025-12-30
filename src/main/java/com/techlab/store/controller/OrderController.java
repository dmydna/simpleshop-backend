package com.techlab.store.controller;

import com.techlab.store.dto.OrderFullDTO;
import com.techlab.store.entity.Order;
import com.techlab.store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders") // Endpoint base
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderFullDTO createOrder(@RequestBody OrderFullDTO order) {
        return this.orderService.createOrder(order);
    }


    @GetMapping("/{id}")
    public OrderFullDTO getOrderById(@PathVariable Long id) {
        return this.orderService.getOrderById(id);
    }

    @GetMapping("/client/{id}")
    public  List<OrderFullDTO> getOrderByClientId(@PathVariable Long id){
        return this.orderService.getOrderByClientId(id);
    }

    @GetMapping
    public List<OrderFullDTO> getAllOrders() {
        return this.orderService.getAllOrders();
    }


    @PutMapping("/{id}/status")
    public OrderFullDTO updateStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderState newState) {
        return this.orderService.updateOrderStatus(id, newState);
    }
}
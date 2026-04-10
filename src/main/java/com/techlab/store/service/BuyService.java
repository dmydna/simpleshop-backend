package com.techlab.store.service;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techlab.store.dto.OrderFullDTO;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.Review;
import com.techlab.store.repository.OrderRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.repository.ReviewRepository;
import com.techlab.store.repository.PendingReviewRepository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuyService {

    @Autowired
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final ClientService clientService; // Asumiendo que existe
    private final OrderService orderService;
    private final PaymentGatewayService paymentGateWayService;
    private final PendingReviewRepository pendingReviewRepositiry;
    private final PendingReviewService pendingReviewService;

    @Transactional
    public boolean confirmPayment(Long orderId, String paymentToken, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrado"));
        
        // Checkeamos token de pasarela valido.
        System.out.println("\n confirmPayment -> Token "+ paymentToken + "\n");
        if (!processPayment(paymentToken)) {
            order.setState(Order.OrderState.CANCELLED);
//            orderService.deleteOrderAndRestoreStock(orderId);
            throw new ValidationException("Payment failed");
        }

        // agregamos pending reviews para cada producto comprado
        order.getDetails().forEach(item -> {
            pendingReviewService
              .create(
                item.getProduct().getId(), 
                order.getClient().getUser().getId(), 
                item.getListingId()
            );
        });

        order.setState(Order.OrderState.PAID);
        orderRepository.save(order);
        return true;
    }



    // Se reserva una orden de compra sin pagar
    // se devuelve objeto para pasarela de pago
    public OrderFullDTO savedOrder(OrderFullDTO dto, Long clientId){
        // Eliminamos ordenes inpagas;
        List<Order> listOrders = orderRepository.findAllByState(Order.OrderState.PENDING);
        for(Order order : listOrders){
            orderService
               .deleteOrderAndRestoreStock(order.getId());
        }
        return orderService
                 .createOrder(dto, clientId);
    }

    // Simulación de pago (en producción usar Stripe, PayPal, etc.)
    public boolean processPayment(String token) {
        // Aquí iría la lógica real de integración con pasarelas
        // Recibo un token y lo confirma.
        // Por ahora, simulamos éxito si el monto es positivo
        return paymentGateWayService.validateToken(token);
    }

    public boolean refundPayment(BigDecimal amount) {
        // Simulación de reembolso
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }




}
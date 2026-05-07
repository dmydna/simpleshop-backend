package com.techlab.store.service;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techlab.store.dto.OrderComplete;
import com.techlab.store.entity.Order;
import com.techlab.store.repository.OrderRepository;
import com.techlab.store.repository.PendingReviewRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.enums.OrderStatus;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuyService {
    @Autowired
    private final OrderService orderService;
    private final ClientService clientService; 
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final PaymentGatewayService paymentGateWayService;
    private final PendingReviewService pendingReviewService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PendingReviewRepository pendingReviewRepositiry;

    @Transactional
    public boolean confirmPayment(Long orderId, String paymentToken, String userEmail) {
        Order order = orderService.getById(orderId);
        // Chequiamos token de pasarela valido.
        System.out.println("\n confirmPayment -> Token "+ paymentToken + "\n");
        if (!processPayment(paymentToken)) {
            order.setStatus(OrderStatus.CANCELLED);
//            orderService.deleteOrderAndRestoreStock(orderId);
            throw new ValidationException("Payment failed");
        }

        // agregamos pending reviews para cada producto comprado
        order.getItems().forEach(item -> {
            pendingReviewService
              .create(
                item.getListing().getProduct().getId(), 
                order.getClient().getUser().getId(), 
                item.getListing().getId()
            );
        });

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        return true;
    }



    // Se reserva una orden de compra sin pagar
    // se devuelve objeto para pasarela de pago
    




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
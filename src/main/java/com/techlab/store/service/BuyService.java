package com.techlab.store.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.techlab.store.entity.Order;
import com.techlab.store.entity.OrderItem;
import com.techlab.store.entity.Review;
import com.techlab.store.repository.OrderItemRepository;
import com.techlab.store.repository.OrderRepository;
import com.techlab.store.enums.OrderStatus;
import com.techlab.store.specification.OrderItemSpecifications;


@Service
@RequiredArgsConstructor
public class BuyService {
    private final OrderService orderService;
    private final PaymentGatewayService paymentGateWayService;
    private final OrderRepository orderRepository;
    private final ReviewService reviewService;
    private final OrderItemRepository orderItemRepository; 

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

        // crea reviews (con status pendiente) para cada producto comprado
        order.getItems().forEach(item -> {

            Review review = reviewService
              .createPendingReview(
                item, 
                order.getClient().getUser() 
            );

            item.setReview(review);  

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


    // Filtrar historial de compras.
    public Page<OrderItem> filter(
            Long userId,
            OrderStatus status,
            Pageable pageable
    ) {
        
        Specification<OrderItem> spec = Specification.allOf(
            OrderItemSpecifications.hasClientId(userId),
            OrderItemSpecifications.hasStatus(status)
        );

        return orderItemRepository.findAll(spec, pageable);
    }


}
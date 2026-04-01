package com.techlab.store.service;


import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techlab.store.dto.OrderFullDTO;
import com.techlab.store.entity.Order;
import com.techlab.store.repository.OrderRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.dto.PaymentRequest;


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


    @Transactional
    public boolean confirmPayment(Long orderId, String paymentToken, Long clientId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrado"));
        // Checkeamos token de pasarela valido.

        if (!processPayment(paymentToken)) {
            order.setState(Order.OrderState.CANCELADO);
            orderService.restoreStockForCanceledOrder(orderId);
            throw new ValidationException("Payment failed");
        }
        order.setState(Order.OrderState.PAGADO);
        return false;
    }


    // Se reserva una orden de compra sin pagar
    // se devuelve objeto para pasarela de pago
    public OrderFullDTO SavedOrder(OrderFullDTO dto, Long clientId){
        return orderService.createOrder(dto, clientId);
    }

    // Simulación de pago (en producción usar Stripe, PayPal, etc.)
    public boolean processPayment(String token) {
        // Aquí iría la lógica real de integración con pasarelas
        // Recibo un token y lo confirma.
        // Por ahora, simulamos éxito si el monto es positivo
        paymentGateWayService.validateToken(token);
        return true;
    }

    public boolean refundPayment(BigDecimal amount) {
        // Simulación de reembolso
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }




}
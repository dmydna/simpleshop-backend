package com.techlab.store.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    // Simulación de pago (en producción usar Stripe, PayPal, etc.)
    public boolean processPayment(String token) {
        // Aquí iría la lógica real de integración con pasarelas
        // Recibo un token y lo confirma.
        // Por ahora, simulamos éxito si el monto es positivo
        return true;
    }

    public boolean refundPayment(BigDecimal amount) {
        // Simulación de reembolso
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
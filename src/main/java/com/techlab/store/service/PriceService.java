package com.techlab.store.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.OrderItem;
import lombok.RequiredArgsConstructor;

import com.techlab.store.dto.DiscountResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceService {

    public DiscountResult calculateDiscount(
        BigDecimal basePrice, Integer discountPercent) {
        
        if(basePrice == null || discountPercent == null){
            throw new IllegalArgumentException("No se puede calcular descuento." + 
                "\n causa: Los argumentos pasados son NULL");
        }

        // Basic validation
        if (basePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }

        // Convert percentage to decimal (e.g., 15 -> 0.15)
        BigDecimal discountDecimal = BigDecimal.valueOf(discountPercent)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Calculate discount amount: Price * (Percentage / 100)
        // Uses RoundingMode.HALF_UP to round to the nearest cent (commercial standard)
        BigDecimal discountAmount = basePrice.multiply(discountDecimal).setScale(2, RoundingMode.HALF_UP);

        // Calculate final price
        BigDecimal finalPrice = basePrice.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);

        return new DiscountResult(basePrice, discountPercent, discountAmount, finalPrice);
    }																																																																																																																																																																																																																																																																		

    public BigDecimal getFinalPrice(Listing listing){
        if(listing == null){
            throw new IllegalArgumentException("No se puede calcular precio final." + 
            "\n causa: Los argumentos pasados son NULL");
        }
        return calculateDiscount(listing.getPrice(), listing.getDiscountPercentage())
        .finalPrice();
    }

    // Validación del precio del item
    public Boolean validateItemPrice(OrderItem orderItem, Listing listing){

        if (orderItem == null || listing == null || orderItem.getPriceAtPurchase() == null 
            || listing.getPrice() == null) {
            return false;
        }
        
        BigDecimal priceAtPurchase = orderItem.getPriceAtPurchase();
        BigDecimal finalPrice = getFinalPrice(listing);  
        return priceAtPurchase.compareTo(finalPrice) == 0;
    }

    // Validación del total de la orden completa
    public Boolean validateTotalAmount(Order order) {
        if (order == null || order.getTotalAmount() == null || order.getItems() == null) {
            return false;
        }

        BigDecimal calculatedTotal = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {
            if (item.getPriceAtPurchase() != null) {
                BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                BigDecimal itemSubtotal = item.getPriceAtPurchase().multiply(quantity);
                calculatedTotal = calculatedTotal.add(itemSubtotal);
            }
        }

        // comparamos el total acumulado en el backend contra lo que envió el frontend
        return calculatedTotal.compareTo(order.getTotalAmount()) == 0;
    }
}

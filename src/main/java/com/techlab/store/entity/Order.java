package com.techlab.store.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@ToString
@Table(name = "T_ORDER")
public class Order {

    // TODO mover OrderState a enums/OrderStatus.java

    public enum OrderState {
        PENDING, COMPLETED, CANCELLED, PAID;

        @JsonCreator
        public static OrderState fromString(String value) {
            if (value == null || value.trim().isEmpty()) return PENDING; // Valor por defecto
            return OrderState.valueOf(value.toUpperCase());
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // enum estado
    @Enumerated(EnumType.STRING)
    private OrderState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientId")
    @JsonIgnoreProperties("orders")
    private Client client;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("order")
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> failedItems = new ArrayList<>();

    public BigDecimal totalAmount;

    // TODO evaluar updateAt
    private LocalDateTime createdAt;

}

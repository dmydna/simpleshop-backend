package com.techlab.store.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    private Set<OrderDetail> details = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private Set<OrderDetail> failedDetails = new HashSet<>();

    public BigDecimal totalAmount;
    private LocalDateTime createdAt;

}

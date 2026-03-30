package com.techlab.store.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@ToString
@Table(name = "T_ORDER")
public class Order {



    public enum OrderState {
        PROCESANDO, COMPLETO, CANCELADO, EN_ENVIO, SIN_PAGAR, PAGADO;

        @JsonCreator
        public static OrderState fromString(String value) {
            if (value == null || value.trim().isEmpty()) return PROCESANDO; // Valor por defecto
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
    private Set<OrderDetail> failedDetails = new HashSet<>();

    public BigDecimal totalAmount;
    private LocalDateTime createdAt;

}

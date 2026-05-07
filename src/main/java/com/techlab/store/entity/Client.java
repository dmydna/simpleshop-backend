package com.techlab.store.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;


// TODO eliminar deleted de client
// TODO actualizar meta 
@Entity
@Getter @Setter
public class Client {
    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private String address;
    private String phone;

    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("client")
    private List<Order> orders = new ArrayList<>();

    @OneToOne
    @MapsId// <-- mismo Id que User
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}

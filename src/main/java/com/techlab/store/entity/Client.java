package com.techlab.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
public class Client {
    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private Boolean deleted;
    private LocalDate deletedDate;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("client")
    private Set<Order> orders = new HashSet<>();

    @OneToOne
    @MapsId// <-- mismo Id que User
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}

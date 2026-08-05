package com.techlab.store.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "FAVORITES")
public class Favorite extends BaseEntity {

    @Column(unique = true, nullable = false, updatable = false)
    private String hash;

    // Relations
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "listing_id")
    @JsonBackReference
    private Listing listing; // opcional

}

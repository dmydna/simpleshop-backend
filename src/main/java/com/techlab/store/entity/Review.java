package com.techlab.store.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating;

    @Column(length = 1000)
    private String comment;

    private LocalDateTime date;

    private String reviewerName;
    private String reviewerEmail;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private Listing listing;
}

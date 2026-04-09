package com.techlab.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.store.entity.Review;
import com.techlab.store.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Crear reseña asociada a un producto
    @PostMapping // SOLO CLIENTES
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Review> create(@PathVariable Long productId, @RequestBody Review review) {
        return ResponseEntity.ok(reviewService.addReviewToProduct(productId, review));
    }

    // Borrar reseña por su ID propio
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        reviewService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
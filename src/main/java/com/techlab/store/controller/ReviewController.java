package com.techlab.store.controller;

import com.techlab.store.entity.Review;
import com.techlab.store.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
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
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        reviewService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
package com.techlab.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.store.dto.PendingReviewDTO;
import com.techlab.store.entity.Review;
import com.techlab.store.entity.User;
import com.techlab.store.service.AuthService;
import com.techlab.store.service.PendingReviewService;
import com.techlab.store.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final PendingReviewService pendingReviewService;
    private final AuthService authService;
    private final ReviewService reviewService;

    // Crear reseña asociada a un producto
    @PostMapping // SOLO CLIENTES
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Review> create(@PathVariable Long productId, @RequestBody Review review) {
        User user = authService.getUser();
        return ResponseEntity.ok(reviewService.addReviewToProduct(
            productId, 
            review, 
            user.getId()
        ));
    }

    // Borrar reseña por su ID propio
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        reviewService.deleteById(id);
        return ResponseEntity.ok().build();
    }



    @DeleteMapping("/pending-review/{productId}")
    public ResponseEntity<?> deletePending(@PathVariable Long productId) {
        User user = authService.getUser();
        boolean isAdmin = authService.isAdmin();
        pendingReviewService.delete(productId, user.getId(), isAdmin);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/pending-review")
    public ResponseEntity<Page<PendingReviewDTO>> getPendingReviews(
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User user = authService.getUser();
        return ResponseEntity.ok(pendingReviewService
            .filter(
                null,  // devuelve todas. 
                user.getId(), 
                authService.isAdmin(), 
                pageable
            ));
    }

}

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

import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.dto.PendingReviewDTO;
import com.techlab.store.entity.Review;
import com.techlab.store.entity.User;
import com.techlab.store.service.AuthService;
import com.techlab.store.service.PendingReviewService;
import com.techlab.store.service.ReviewService;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final PendingReviewService pendingReviewService;
    private final AuthService authService;
    private final ReviewService reviewService;

    // Crear reseña asociada a un producto
    @PostMapping// SOLO CLIENTES
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<ReviewDTO> create(@RequestBody ReviewDTO review) {
        User user = authService.getUser();
        return ResponseEntity.ok(reviewService.addReviewToProduct(
            review, 
            user
        ));
    }

    // Borrar reseña por su ID propio
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        User user = authService.getUser();
        reviewService.deleteById(id, user.getUsername());
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
        @RequestParam(required = false) Long id,
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) Long productId,
        @RequestParam(required = false) Boolean active,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User user = authService.getUser();
        if (authService.isAdmin()) {
            return ResponseEntity.ok(pendingReviewService
                    .findByFilter(id, userId, productId, active ,pageable));
        }
         return ResponseEntity.ok(pendingReviewService
                    .findByFilter(id, user.getId(), productId, false ,pageable));
    }

}

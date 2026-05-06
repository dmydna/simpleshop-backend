package com.techlab.store.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.store.dto.PendingReviewDTO;
import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.entity.PendingReview;
import com.techlab.store.entity.Review;
import com.techlab.store.entity.User;
import com.techlab.store.mapper.ReviewMapper;
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
    private final ReviewMapper reviewMapper;

    // Crear reseña asociada a un producto
    @PostMapping// SOLO CLIENTES
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<ReviewDTO> create(@RequestBody ReviewDTO review) {
        Review entity = reviewService
            .addReviewToProduct(review, authService.getUser());
        ReviewDTO response = reviewMapper.toDto(entity);
        return ResponseEntity.ok(response);
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
        Page<PendingReview> reviewsPage;
        if (authService.isAdmin()) {
            reviewsPage = pendingReviewService
                .filter(id, userId, productId, active ,pageable);
        }else{
            User user = authService.getUser();
            reviewsPage = pendingReviewService
                .filter(id, user.getId(), productId, false ,pageable);
        }
        Page<PendingReviewDTO> response = reviewsPage
            .map(pending-> reviewMapper.pendingToDto(pending));

         return ResponseEntity.ok(response);
    }

}

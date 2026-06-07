package com.techlab.store.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.dto.ReviewRequest;
import com.techlab.store.dto.UpdateReview;
import com.techlab.store.enums.ReviewStatus;
import com.techlab.store.entity.Review;
import com.techlab.store.entity.User;
import com.techlab.store.mapper.ReviewMapper;
import com.techlab.store.service.AuthService;
import com.techlab.store.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final AuthService authService;
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;
    // Crear reseña asociada a un producto
    @PostMapping
    public ResponseEntity<ReviewDTO> create(@RequestBody ReviewDTO review) {
        Review entity = reviewService
            .create(review.productId(), authService.getUser().getId(), review.listingId());
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



    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
        @PathVariable Long id, 
        @RequestBody UpdateReview data
    ) {
        User user = authService.getUser();
        boolean isAdmin = authService.isAdmin();

        Review review = reviewService.getById(id);
        if(review.getUser().getId() != user.getId() && !isAdmin){
            throw new RuntimeException("No tiene permisos para actualizar Review") ;
        }


        Review entity = new Review();
        entity.setStatus(data.status());
        entity.setComment(data.comment());
        entity.setRating(data.rating());

        reviewService.updateReviewById(id, entity);
        return ResponseEntity.ok(Map.of("message", "Review actualizada correctamente"));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getById(@PathVariable Long id){
        Review entity = reviewService.getById(id);
        User user = authService.getUser();
        if(entity.getUser().getId() != user.getId()){
            throw new RuntimeException("No tiene permisos para leer esta Review") ; 
        }
        ReviewDTO response = reviewMapper.toDto(entity);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/requests")
    public ResponseEntity<Page<ReviewRequest>> getPendingReviews(
        @RequestParam(required = false) Long id,
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) Long productId,
        @RequestParam(required = false) ReviewStatus status,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Review> reviewsPage;
        if (authService.isAdmin()) {
            ReviewStatus finalStatus = status != null ? status : ReviewStatus.PENDING;
            reviewsPage = reviewService.filter(id, userId, productId, finalStatus, pageable);
        } else {
            User user = authService.getUser();
            reviewsPage = reviewService.filter(id, user.getId(), productId, ReviewStatus.PENDING, pageable);
        }
        // Nota: los request son eliminados cuando el listing ya no esta disponible.
        Page<ReviewRequest> response = reviewsPage
            .map(review -> reviewService.getReviewRequest(review.getId())); // excluye request vencidos

        return ResponseEntity.ok(response);
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<ReviewRequest> getReviewRequest(@PathVariable Long id){
        Review entity = reviewService.getById(id);
        User user = authService.getUser();
        if(entity.getUser().getId() != user.getId()){
            throw new RuntimeException("No tiene permisos para leer esta Review") ; 
        }
        ReviewRequest request = reviewService.getReviewRequest(id);
        if(request.hash() == null){
           throw new RuntimeException("Review Request (id: "+ request.id() + ") vencido o invalido") ;  
        }
        return ResponseEntity.ok(request);
    }



}

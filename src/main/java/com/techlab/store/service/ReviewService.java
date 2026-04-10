package com.techlab.store.service;


import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.repository.ReviewRepository;
import com.techlab.store.utils.StringUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import com.techlab.store.entity.PendingReview;
import com.techlab.store.repository.PendingReviewRepository;


@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final PendingReviewService pendingReviewService;
    private final PendingReviewRepository pendingReviewRepository;

    private StringUtils stringUtils;

    public void deleteById(Long id) {
    }

    public Review addReviewToProduct(Long  productId, Review review, Long userId) {


        try {
            pendingReviewService.delete(productId, userId, false);
        } catch (Exception e) {
             System.out.println("Error al eliminar review pendiente: " + e.getMessage());
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (review.getDate() == null) {
            review.setDate(LocalDateTime.now());
        }

        review.setProduct(product);
        product.getReviews().add(review);

        return this.reviewRepository.save(review);
    }
}

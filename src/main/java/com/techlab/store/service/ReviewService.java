package com.techlab.store.service;


import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.repository.ReviewRepository;
import com.techlab.store.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private StringUtils stringUtils;


    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    public void deleteById(Long id) {
    }

    public Review addReviewToProduct(Long productId, Review review) {
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

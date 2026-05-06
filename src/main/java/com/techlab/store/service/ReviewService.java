package com.techlab.store.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.techlab.store.dto.OrderComplete;
import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.entity.PendingReview;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.entity.User;
import com.techlab.store.exceptions.CustomExceptions.ProductHasDeletedException;
import com.techlab.store.exceptions.CustomExceptions.ReviewExpiratedException;
import com.techlab.store.repository.PendingReviewRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.repository.ReviewRepository;
import com.techlab.store.utils.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final PendingReviewService pendingReviewService;
    private final PendingReviewRepository pendingReviewRepository;
    private StringUtils stringUtils;


    public void updateProductRating(Product product, Review review){
        log.info("🔔 Actualizando Rating de producto ..." );
        Double rating = calcRating(product.getId());
        product.setRating(rating);
    }


    public Double calcRating(Long id){
        log.info("🔔 Calculando Rating de producto ..." );
        List<Review> reviews = reviewRepository.findByProductId(id);
        Double totalRating = 0.0;
        Integer totalReview = reviews.size();
        for(Review rev : reviews){
            totalRating += rev.getRating();
        }
        return (totalRating / totalReview);
    }

    public Review addReviewToProduct(ReviewDTO review, User user) {

        log.info("🔔 Creando Review ..." );

        Product product = productRepository.findById(review.productId())
                .orElseThrow(() -> new ProductHasDeletedException(review.productId()));

        PendingReview pending = pendingReviewService.getPendingReview(review.productId(), user.getId());
        if (pending.isReviewed() == true) {
            throw new ReviewExpiratedException(pending.getId());
        }
        // softdelete
        if (pending.getUser().getId() == user.getId()) {
            log.info("🔔 pendingReview fue eliminado");
            pending.setReviewed(true);
        } 
        
        Review savedReview = reviewRepository
               .save(dtoToReview(review, user, product));

        updateProductRating(product, savedReview);
        return savedReview;
    }

    public void deleteById(Long productId, String username) {
        Review review = reviewRepository.findByProductIdAndReviewerName(productId, username)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        reviewRepository.delete(review);
    }

    public Review dtoToReview(ReviewDTO review, User user, Product product) {
        Review entity = new Review();
        entity.setComment(review.comment());
        entity.setRating(review.rating());
        entity.setUser(user);
        entity.setProduct(product);
        return entity;
    }

    public ReviewDTO entityToDto(Review entity) {
        ReviewDTO dto = new ReviewDTO(
            entity.getId(),
            entity.getUser().getUsername(),
            entity.getUser().getEmail(),
            entity.getRating(),
            entity.getComment(),
            entity.getProduct().getId()
        );
        return dto;
    }

}

package com.techlab.store.service;


import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.entity.User;
import com.techlab.store.dto.ReviewDTO;
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

public ReviewDTO addReviewToProduct(ReviewDTO review, User user) {

System.out.println("Entra en addReviewToProduct: ");

    Product product = productRepository.findById(review.productId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));


   System.out.println("try PendingReview con product_id " +  review.productId()  + ", and user_id " +  user.getId());
   PendingReview pending = pendingReviewService.getPendingReview(review.productId(), user.getId());
   if(pending.isReviewed() == true){
      System.out.println("pendingReview vencio.");
      return review; // no guarda
    }
    // softdelete
    if(pending.getUser().getId() == user.getId()){
         System.out.println("borra pendingReview");
         pending.setReviewed(true);
    }else {
        System.out.println("Intenta borrar pendingReview de otro user");
        return review;
     }

    Review savedReview = this.reviewRepository.save(dtoToReview(review, user, product));
    return entityToDto(savedReview);
}


public void deleteById(Long productId, String username){
   Review review = reviewRepository.findByProductIdAndReviewerName(productId, username)
         .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
   reviewRepository.delete(review);
}

public Review dtoToReview(ReviewDTO review, User user, Product product) {
    Review entity = new Review();
    entity.setComment(review.comment());
    entity.setRating(review.rating());
    entity.setDate(LocalDateTime.now());
    entity.setReviewerEmail(user.getEmail());
    entity.setReviewerName(user.getUsername());
    entity.setProduct(product);
    return entity;
}

public ReviewDTO entityToDto(Review entity) {
    ReviewDTO dto = new ReviewDTO(
      entity.getId(),
      entity.getReviewerName(),
      entity.getReviewerEmail(),
      entity.getRating(),
      entity.getComment(),
      entity.getProduct().getId()
    );
    return dto;
}



}

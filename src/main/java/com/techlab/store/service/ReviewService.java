package com.techlab.store.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.dto.ReviewRequest;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.entity.User;
import com.techlab.store.enums.ReviewStatus;
import com.techlab.store.mapper.ReviewMapper;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.repository.ReviewRepository;
import com.techlab.store.repository.UserRepository;
import com.techlab.store.specification.ReviewSpecifications;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ListingService listingService;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ListingRepository listingRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;


    @Transactional
    public void updateProductRating(Product product){
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


    public Review create(Long productId, Long userId, Long listingId) {

      // Si ya existe, lo retorna directamente
       Optional<Review> existing 
            = reviewRepository.findOneByUserIdAndProductId(userId,productId);

       if (existing.isPresent()) {return existing.get();}

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing no encontrado"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User no encontrado"));


        Review review = new Review();
        review.setProduct(product);
        review.setListingId(listing.getId());
        review.setUser(user);
        review.setStatus(ReviewStatus.PENDING);
        return  reviewRepository.save(review);
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


    // FIXME: si review es valida, si no encuentra listing entonces falla 
    public ReviewRequest getReviewRequest(Long id) {
       Review review = getById(id);
       Listing listing = null;
       try { // Si el listing no existe, esto lanzará una excepción
           listing = listingService.getById(review.getListingId());
       } catch (Exception e) {
           log.warn("Review request (id: {}) vencio porque Listing no fue encontrado.", review.getId());
       }
       return new ReviewRequest(
            review.getId(),
            listing != null ? listing.getHash() : null,
            listing != null ? listing.getTitle() : null,
            listing != null ? listing.getPrice() : null,
            listing != null ? listing.getThumbnail() : null
       );
    }



    @Transactional
    public Review updateReviewById(Long id, Review dataToUpdate){
        log.info("🔔 actualizando review {}...", dataToUpdate);
        Review review = getById(id);
        if(dataToUpdate.getStatus() != null){ 
            updateSatusById(id, dataToUpdate.getStatus());
        }
        review.setUpdatedAt(LocalDateTime.now());
        Review savedReview = reviewMapper.updateFromEntity(dataToUpdate, review);
        if(savedReview.getStatus().equals(ReviewStatus.ACTIVE)){
            Product product = productService.getById(review.getProduct().getId());
            updateProductRating(product);
        }

        return savedReview;

    }

    @Transactional
    public Review updateSatusById(Long id, ReviewStatus status){
        log.info("🔔 actualizando status de listing con ID {}...", id);
        Review review = getById(id);

        if(isDeleted(id)){ 
           throw new RuntimeException("Review no encontrada") ;
        }

        if(status.equals(ReviewStatus.DELETED)){ 
            deleteById(id); 
        }

        review.setStatus(status);

        return review;
    }


    public void deleteById(Long id) {
        log.info("🔔 Eliminando review con ID {}...", id);
        Review review = getById(id);
        review.setStatus(ReviewStatus.DELETED);
        review.setDeletedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }


    public boolean isDeleted(Long id){
        Review entity = getById(id);
        return entity.getDeletedAt() != null;
    }

    public Review getByUserId(Long id){
        Review review = this.reviewRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Review no encontrada"));
        if (review.getDeletedAt() != null) {
            throw new RuntimeException("Review con Id: "+ id +" fue Eliminado");
        }
        return review;
    }



    public Review getById(Long id){
        Review review = this.reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review no encontrada"));
        if (review.getDeletedAt() != null) {
            throw new RuntimeException("Review con Id: "+ id +" fue Eliminado");
        }
        return review;
    }



    public Page<Review> filter(
          Long id, 
          Long userId, 
          Long productId, 
          ReviewStatus status,
          Pageable pageable) {

        Specification<Review> spec = Specification
                .where(ReviewSpecifications.hasStatus(status))
                .and(ReviewSpecifications.hasUserId(userId))
                .and(ReviewSpecifications.hasProductId(productId))
                .and(ReviewSpecifications.hasId(id));

        return reviewRepository.findAll(spec, pageable);
    }





}

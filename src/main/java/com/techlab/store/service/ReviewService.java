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


@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ListingRepository listingRepository;
    private StringUtils stringUtils;

    public void deleteById(Long id) {
    }

    public Review addReviewToProduct(Long listingId, Review review) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (review.getDate() == null) {
            review.setDate(LocalDateTime.now());
        }

        review.setListing(listing);
        listing.getReviews().add(review);

        return this.reviewRepository.save(review);
    }
}

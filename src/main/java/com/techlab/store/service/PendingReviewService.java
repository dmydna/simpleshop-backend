package com.techlab.store.service;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.techlab.store.dto.PendingReviewDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Favorite;
import com.techlab.store.entity.PendingReview;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.User;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.repository.PendingReviewRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.repository.UserRepository;
import org.springframework.data.jpa.domain.Specification;
import com.techlab.store.specification.PendingReviewSpecifications;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class PendingReviewService {
    private final PendingReviewRepository pendingReviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    public PendingReview create(Long productId, Long userId, Long listingId) {

      // Si ya existe, lo retorna directamente
       Optional<PendingReview> existing 
            = pendingReviewRepository.findOneByUserIdAndProductId(userId,productId);
       if (existing.isPresent()) {return existing.get();}

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing no encontrado"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User no encontrado"));
    


        PendingReview pendingReview = new PendingReview();
        pendingReview.setProduct(product);
        pendingReview.setUser(user);
        pendingReview.setReviewed(false);
        pendingReview.setListing(listing);
        return  pendingReviewRepository.save(pendingReview);
    }


    public Page<PendingReview> filter(
          Long id, 
          Long userId, 
          Long productId, 
          Boolean active,
          Pageable pageable) {

        Specification<PendingReview> spec = Specification
                .where(PendingReviewSpecifications.hasActive(active))
                .and(PendingReviewSpecifications.hasUserId(userId))
                .and(PendingReviewSpecifications.hasProductId(productId))
                .and(PendingReviewSpecifications.hasId(id));

        return pendingReviewRepository.findAll(spec, pageable);
    }


    public Page<PendingReviewDTO> findByFilter(Long id, Long userId, Long productId, Boolean active, Pageable pageable){
          
        return filter(id, userId, productId, active, pageable)
            .map(pr -> reviewToDTO(pr));
    }



    public PendingReviewDTO reviewToDTO(PendingReview pendingReview) {


        return new PendingReviewDTO(    
            pendingReview.getId(),
            pendingReview.getProduct().getId(),
            pendingReview.getUser().getId(),
            pendingReview.getListing().getId(),
            pendingReview.getListing().getThumbnail(),
            pendingReview.getListing().getTitle()
        );
    }


   public PendingReview getPendingReview(Long productId, Long userId){
        return pendingReviewRepository.findOneByUserIdAndProductId(userId, productId).orElseThrow(() -> new RuntimeException("PendingReview no encontrado"));
   }


    public void delete(Long productId, Long userId, boolean isAdmin) {
        PendingReview pendingReview = pendingReviewRepository.findOneByUserIdAndProductId(userId, productId).orElseThrow(() -> new RuntimeException("PendingReview no encontrado"));

        // Si no es admin, verificar que pertenece al usuario
        if (!isAdmin && !pendingReview.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para eliminar este pending review");
        }

        pendingReview.setReviewed(true);
    }


}

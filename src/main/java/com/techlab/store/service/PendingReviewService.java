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



    // Nota: 
    // 1. solo funciona para filtrar por por id, no ambos a la vez
    // 2. el userId es obligatorio para asegurar que el usuario solo vea sus pending reviews, a menos que sea admin, en ese caso se ignora el userId y se muestran todos los pending reviews
    public Page<PendingReviewDTO> filter(
        Long id, 
        Long userId, 
        boolean isAdmin, 
        Pageable pageable) {

        if(id != null && !isAdmin){
             // devuelve un elmento (del usuario)
            return pendingReviewRepository
              .findAllByIdAndUserId(id, userId, pageable)
              .map(this::reviewToDTO);
        } 

        if(userId != null && id == null && !isAdmin){
             // devuelve una lista de elementos(del usuario)
             return pendingReviewRepository
                 .findAllByUserId(userId, pageable)
                 .map(this::reviewToDTO);
        }

        return pendingReviewRepository.findAll(pageable)
                .map(this::reviewToDTO);

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



    public void delete(Long productId, Long userId, boolean isAdmin) {
        PendingReview pendingReview = pendingReviewRepository.findOneByUserIdAndProductId(userId,productId).orElseThrow(() -> new RuntimeException("PendingReview no encontrado"));

        // Si no es admin, verificar que pertenece al usuario
        if (!isAdmin && !pendingReview.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para eliminar este pending review");
        }

        pendingReviewRepository.delete(pendingReview);
    }


}

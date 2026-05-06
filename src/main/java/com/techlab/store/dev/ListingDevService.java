package com.techlab.store.dev;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import com.techlab.store.dto.ListingDTO; 
import com.techlab.store.dto.CreateListingDTO;
import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.enums.Status;
import com.techlab.store.mapper.ListingMapper;
import com.techlab.store.mapper.ReviewMapper;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.repository.ReviewRepository;
import com.techlab.store.service.FileStorageService;
import com.techlab.store.utils.HashUtil;
import com.techlab.store.utils.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ListingDevService {

    private final ListingRepository listingRepository;
    private final StringUtils stringUtils;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    private final ListingMapper listingMapper;
    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;

    @Transactional
    public List<ListingDTO> saveAll(List<ListingDTO> dtos) {
        log.info("\n\n >> Iniciando persistencia masiva de {} elementos \n\n", dtos.size());
        // 1. Convertimos los DTOs a Entidades preparadas
        List<Listing> listingsToSave = 
              dtos.stream()
                  .map(dto -> createListingFromDto(dto))
                  .collect(Collectors.toList());
        // 2. Guardamos todos los Listings de una vez
        List<Listing> savedListings = listingRepository.saveAll(listingsToSave);
        log.info("\n\n >> Guarda Lista masiva de {} elementos \n\n", dtos.size());
        // 3. Retornamos la lista convertida a DTO para el Frontend
        return savedListings.stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }


public Listing createListingFromDto(ListingDTO dto) {
    Listing listing = listingMapper.toEntity(dto);
    
    Product finalProduct = productRepository.findBySku(dto.sku())
        .orElseGet(() -> {
            Product newProduct = listing.getProduct();
            newProduct.setStatus(Status.ACTIVE);
            newProduct.setCreatedAt(LocalDate.now());
            Product savedProduct = productRepository.save(newProduct);
            log.info("✅ Product guardado con ID: {}", savedProduct.getId());
            return savedProduct;
        });
    
    listing.setProduct(finalProduct);
    listing.setHash(HashUtil.generateShortHash());
    listing.setCreatedAt(LocalDate.now());
    listing.setStatus(Status.ACTIVE);
    saveReviewsFromDTO(dto.reviews(), finalProduct);
    return listing;
}


    public List<Review> saveReviewsFromDTO(List<ReviewDTO> review, Product product){
        List<Review> reviewsToSave = 
            review.stream()
                .map(r -> reviewMapper.toEntity(r, product))
                .collect(Collectors.toList());
        reviewRepository.saveAll(reviewsToSave);
        return reviewsToSave;
    }
}

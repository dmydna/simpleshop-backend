package com.techlab.store.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.techlab.store.dto.ListingDTO;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.utils.ListingMapper;
import com.techlab.store.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class ListingService {

    private final ListingRepository listingRepository;
    private final StringUtils stringUtils;
    private final ProductRepository productRepository;

    @Autowired
    private ListingMapper listingMapper;

    public ListingService(ListingRepository listingRepository, StringUtils stringUtils, ProductRepository productRepository) {
        this.listingRepository = listingRepository;
        this.stringUtils = stringUtils;
        this.productRepository = productRepository;
    }

    public ListingDTO createFastListing(ListingDTO dto) {
        log.info("Publicacion ingresada: {}", dto);

        Product newProduct = new Product();
        newProduct.setSku(dto.getSku());
        newProduct.setBrand(dto.getBrand());
        newProduct.setWeight(dto.getWeight());
        newProduct.setDimensions(dto.getDimensions());
        newProduct.setStock(dto.getStock());
        newProduct.setCategory(dto.getCategory());
        newProduct.setTags(dto.getTags());
        newProduct.setMeta(dto.getMeta());

        productRepository.save(newProduct);

        Listing newListing = this.listingMapper.toEntity(dto);

        Set<Review> reviews = new HashSet<>();
        Set<Review> reviewsNode = dto.getReviews();
        for (Review rNode : reviewsNode) {
            Review review = new Review();
            review.setRating(rNode.getRating());
            review.setComment(rNode.getComment());
            review.setReviewerName(rNode.getReviewerName());
            review.setListing(newListing); // Vincular review a la publicación
            reviews.add(review);
        }

        newListing.setReviews(reviews);
        Listing savedListing  = this.listingRepository.save(newListing);
        return listingMapper.toDto(savedListing);
    }

    public ListingDTO createListing(ListingDTO dto, Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("No encontrado"));

        dto.setProduct_id(product.getId());
        dto.setSku(product.getSku());
        dto.setBrand(product.getBrand());
        dto.setWeight(product.getWeight());
        dto.setDimensions(product.getDimensions());
        dto.setStock(product.getStock());
        dto.setCategory(product.getCategory());
        dto.setTags(product.getTags());
        dto.setMeta(product.getMeta());

        Listing newListing = this.listingMapper.toEntity(dto);

        Set<Review> reviews = new HashSet<>();
        Set<Review> reviewsNode = dto.getReviews();
        for (Review rNode : reviewsNode) {
            Review review = new Review();
            review.setRating(rNode.getRating());
            review.setComment(rNode.getComment());
            review.setReviewerName(rNode.getReviewerName());
            review.setListing(newListing); // Vincular review a la publicación
            reviews.add(review);
        }

        newListing.setReviews(reviews);
        Listing savedListing  = this.listingRepository.save(newListing);
        return listingMapper.toDto(savedListing);
    }


    public ListingDTO getListingById(Long id){
        Listing listing = this.listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
        return this.listingMapper.toDto(listing);
    }

    public List<ListingDTO> findAllListing(){
        List<Listing> listings = this.listingRepository.findAll();
        return this.listingMapper.toDtoList(listings);
    }

    public ListingDTO editListingById(Long id, Listing dataToEdit) {
        Listing listing = this.listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));

        if (!stringUtils.isEmpty(dataToEdit.getTitle())){
            System.out.printf("Editando el nombre del producto: viejo:%s - nuevo:%s", listing.getTitle(), dataToEdit.getTitle());
            listing.setTitle(dataToEdit.getTitle());
        }
        if (!stringUtils.isEmpty(dataToEdit.getDescription()))
            listing.setDescription(dataToEdit.getDescription());
        if (null != dataToEdit.getDeleted())
            listing.setDeleted(dataToEdit.getDeleted());

        listing.setPrice(dataToEdit.getPrice());
        listing.setDiscountPercentage(dataToEdit.getDiscountPercentage());
        listing.setRating(dataToEdit.getRating());
        listing.setWarrantyInformation(dataToEdit.getWarrantyInformation());
        listing.setShippingInformation(dataToEdit.getShippingInformation());
        listing.setAvailabilityStatus(dataToEdit.getAvailabilityStatus());
        listing.setReviews(dataToEdit.getReviews());
        listing.setReturnPolicy(dataToEdit.getReturnPolicy());
        listing.setMinimumOrderQuantity(dataToEdit.getMinimumOrderQuantity());
        listing.setImages(dataToEdit.getImages());
        listing.setThumbnail(dataToEdit.getThumbnail());

        Listing saveListing = this.listingRepository.save(listing);
        return this.listingMapper.toDto(saveListing);
    }



    public ListingDTO deleteListingById(Long id) {
        Listing listing = this.listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));

        //this.productRepository.delete(post);
        listing.setDeleted(true);
        Listing saveListing = this.listingRepository.save(listing);
        return this.listingMapper.toDto(saveListing);
    }

    @Transactional
    public List<ListingDTO> saveAll(List<ListingDTO> listings) {
        for (ListingDTO listing : listings) {
            if (listing.getReviews() != null) {
                listing.getReviews().forEach(review ->
                        review.setListing(this.listingMapper.toEntity(listing)));
            }
        }

        List<Listing> entityList = this.listingMapper.toEntityList(listings);
        List<Listing> saveListings = listingRepository.saveAll(entityList);
        return  this.listingMapper.toDtoList(saveListings);
    }
}

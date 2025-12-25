package com.techlab.store.service;

import com.techlab.store.entity.Listing;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ListingService {

    private final ListingRepository listingRepository;
    private final StringUtils stringUtils;

    public ListingService(ListingRepository listingRepository, StringUtils stringUtils) {
        this.listingRepository = listingRepository;
        this.stringUtils = stringUtils;
    }

    public Listing createListing(Listing listing) {
        log.info("Publiacacion ingresada: {}", listing);
        return this.listingRepository.save(listing);
    }



    public Listing getListingById(Long id){
        Optional<Listing> listingOptional = this.listingRepository.findById(id);

        if (listingOptional.isEmpty()){
            throw new RuntimeException("Publiacacion no encontrada con ID: " + id);
        }

        return listingOptional.get();
    }

    public List<Listing> findAllListing(String name, String category){
        if (!name.isEmpty() && !category.isEmpty()){
            return this.listingRepository.findByTitleContainingIgnoreCaseAndCategoryContainingIgnoreCase(name, category);
        }

        if (!name.isEmpty()){
            return this.listingRepository.findByTitleContainingIgnoreCase(name);
        }

        if (!category.isEmpty()){
            return this.listingRepository.findByCategoryContainingIgnoreCase(category);
        }

        return this.listingRepository.findAll();
    }

    public Listing editListingById(Long id, Listing dataToEdit) {
        Listing listing = this.getListingById(id);

        if (!stringUtils.isEmpty(dataToEdit.getTitle())){
            System.out.printf("Editando el nombre del producto: viejo:%s - nuevo:%s", listing.getTitle(), dataToEdit.getTitle());
            listing.setTitle(dataToEdit.getTitle());
        }
        if (!stringUtils.isEmpty(dataToEdit.getDescription())) listing.setDescription(dataToEdit.getDescription());
        if (null != dataToEdit.getDeleted()) listing.setDeleted(dataToEdit.getDeleted());
        listing.setPrice(dataToEdit.getPrice());
        listing.setRating(dataToEdit.getRating());
        return this.listingRepository.save(listing);
    }



    public Listing deleteListingById(Long id) {
        Listing listing = this.getListingById(id);

        //this.productRepository.delete(post);
        listing.setDeleted(true);
        this.listingRepository.save(listing);

        return listing;
    }

    @Transactional
    public List<Listing> saveAll(List<Listing> listings) {
        for (Listing listing : listings) {
            if (listing.getReviews() != null) {
                listing.getReviews().forEach(review -> review.setListing(listing));
            }
        }
        return listingRepository.saveAll(listings);
    }
}

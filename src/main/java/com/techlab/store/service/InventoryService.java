package com.techlab.store.service;

import com.techlab.store.entity.Product;
import com.techlab.store.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.sun.source.tree.LabeledStatementTree;
import com.techlab.store.entity.Listing;
import com.techlab.store.repository.ListingRepository;

@Service
@RequiredArgsConstructor
public class InventoryService {

    @Autowired
    private final ListingRepository listingRepository;
    
    @Transactional
    public boolean decreaseStock(Long listingId, Integer quantity) {
        Optional<Listing> listingOpt = listingRepository.findById(listingId);


        if (listingOpt.isEmpty()) {
            return false;
        }

        Listing listing = listingOpt.get();
        if (listing.getStock() < quantity) {
            return false;
        }

        if(listing.getStock() == 0){
            listing.setAvailabilityStatus("Out of Stock");
        }

        if(listing.getStock() < 10){
            listing.setAvailabilityStatus("Low Stock");
        }

        if(listing.getStock() >= 10){
            listing.setAvailabilityStatus("In Stock");
        }

        listing.setStock(listing.getStock() - quantity);
        listingRepository.save(listing);

        return true;
    }
}
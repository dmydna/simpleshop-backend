package com.techlab.store.controller;

import com.techlab.store.entity.Listing;
import com.techlab.store.service.ListingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listing")

public class ListingController {
    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping
    public Listing createListing(@RequestBody Listing listing){
        return this.listingService.createListing(listing);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Listing>> createPosts(@RequestBody List<Listing> listings) {
        // El service debe usar saveAll()
        listings.forEach(listing -> listing.setId(null));
        List<Listing> savedListings = listingService.saveAll(listings);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedListings);
    }

    @GetMapping("/{id}")
    public Listing getPostById(@PathVariable Long id){
        return this.listingService.getListingById(id);
    }

    @GetMapping
    public List<Listing> getAllPost(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String category){
        return this.listingService.findAllListing(name, category);
    }

    @PutMapping("/{id}")
    public Listing editPostById(@PathVariable Long id, @RequestBody Listing dataToEdit){
        return this.listingService.editListingById(id, dataToEdit);
    }

    @DeleteMapping("/{id}")
    public Listing deletePostById(@PathVariable Long id){
        return this.listingService.deleteListingById(id);
    }

}

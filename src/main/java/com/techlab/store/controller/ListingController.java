package com.techlab.store.controller;

import com.techlab.store.dto.ListingDTO;
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
    public ListingDTO createListing(@RequestBody ListingDTO listing){
        return this.listingService.createListing(listing);
    }


    @PostMapping("/bulk")
    public ResponseEntity<List<ListingDTO>> createPosts(@RequestBody List<ListingDTO> listings) {
        // El service debe usar saveAll()
        listings.forEach(listing -> listing.setId(null));
        List<ListingDTO> savedListings = listingService.saveAll(listings);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedListings);
    }

    @GetMapping("/{id}")
    public ListingDTO getPostById(@PathVariable Long id){
        return this.listingService.getListingById(id);
    }

    @GetMapping
    public List<ListingDTO> getAllPost(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String category){
        return this.listingService.findAllListing();
    }

    @PutMapping("/{id}")
    public ListingDTO editPostById(@PathVariable Long id, @RequestBody Listing dataToEdit){
        return this.listingService.editListingById(id, dataToEdit);
    }

    @DeleteMapping("/{id}")
    public ListingDTO deletePostById(@PathVariable Long id){
        return this.listingService.deleteListingById(id);
    }

}

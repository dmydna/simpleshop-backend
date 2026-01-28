package com.techlab.store.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.techlab.store.dto.ListingDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.enums.Visibility;
import com.techlab.store.service.ListingService;


@RestController
@RequestMapping("/api/listing")

public class ListingController {
    private final ListingService listingService;
    @Value("${app.base-url}")
    private String baseUrl;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ListingDTO> create(
        @RequestPart("listing") ListingDTO listingDTO,
        @RequestPart(value = "files", required = false) MultipartFile[] files
    ) {
        ListingDTO savedListing = listingService.create(listingDTO, files);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedListing);
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
    public ListingDTO getById(@PathVariable Long id){
        return this.listingService.getById(id);
    }


    @GetMapping("/hash/{hash}")
    public ResponseEntity<Map<String, Object>> getByHash(@PathVariable String hash) {
        Map<String, Object> response = new HashMap<>();
        response.put("listing", listingService.getByHash(hash));
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<Page<ListingDTO>> getAll(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) List<String> categories,
        @RequestParam(required = false) List<String> tags,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // El Service decide si usa filtros o si devuelve todo
        return ResponseEntity.ok(listingService.search(title, categories, tags, minPrice, maxPrice, pageable));
    }



    @PutMapping("/{id}")
    public ListingDTO updateById(@PathVariable Long id, @RequestBody Listing dataToEdit){
        return this.listingService.updateById(id, dataToEdit);
    }

    @DeleteMapping("/{id}")
    public ListingDTO deleteById(@PathVariable Long id){
        return this.listingService.deleteById(id);
    }


    @PostMapping("/{id}/upload-single")
    public ResponseEntity<?> uploadSingle(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            String url = listingService.uploadImage(id, file);
            return ResponseEntity.ok(url);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }


    @PostMapping("/{id}/upload-multiple")
    public ResponseEntity<?> uploadMultiple(@PathVariable Long id, @RequestParam("files") MultipartFile[] files) {
        try {
            List<String> urls = listingService.uploadImages(id, files);
            return ResponseEntity.ok(urls);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al procesar lote: " + e.getMessage());
        }
    }



    @DeleteMapping("/{id}/images")
    public ResponseEntity<?> deleteImage(
            @PathVariable Long id,
            @RequestParam String imageUrl // El front envía la URL completa de la imagen a borrar
    ) {
        listingService.removeImageFromListing(id, imageUrl);
        return ResponseEntity.ok("Imagen eliminada correctamente");
    }

    @PatchMapping("/{id}/visibility")
    public ResponseEntity<ListingDTO> updateVisibility( @PathVariable Long id, @RequestParam Visibility visibility) {
        return ResponseEntity.ok(listingService.changeVisibility(id, visibility));
    }
}

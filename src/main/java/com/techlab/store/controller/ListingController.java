package com.techlab.store.controller;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.techlab.store.dto.CreateListingDTO;
import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.UpdateListingDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.enums.Status;
import com.techlab.store.mapper.ListingMapper;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.service.AuthService;
import com.techlab.store.service.ListingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/listing")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;
    private final ListingMapper listingMapper;
    private final ProductRepository productRepository;
    private final AuthService authService;

    @Value("${app.base-url}")
    private String baseUrl;


    // CREATE
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ListingDTO> create(
        @RequestPart("listing") CreateListingDTO dto,
        @RequestPart(value = "files", required = false) MultipartFile[] files
    ) {
        Listing entity = listingMapper.toEntity(dto);
        Listing saveListing = listingService.create(entity, files); 
        ListingDTO response = listingMapper.toDto(saveListing);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // CREATE DRAFT
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value="/draft",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ListingDTO> createDraft(
        @RequestPart("listing") CreateListingDTO dto,
        @RequestPart(value = "files", required = false) MultipartFile[] files
    ) {
        Listing entity = listingMapper.toEntity(dto);
        entity.setStatus(Status.DRAFT); // <-- importante
        Listing saveListing = listingService.create(entity, files); 
        ListingDTO response = listingMapper.toDto(saveListing);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // GET
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ListingDTO> getById(@PathVariable Long id){
        Listing entity = listingService.getById(id);
        ListingDTO response = listingMapper.toDto(entity);
        return ResponseEntity.ok(response);
    }

    // GET
    // La busqueda por hash solo devuelve elementos publicos para cliente
    @GetMapping("/hash/{hash}")
    public ResponseEntity<Map<String, Object>> getByHash(@PathVariable String hash) {
        Listing entity = listingService.getByHash(hash);
        Status status = entity.getStatus();
        if(!authService.isAdmin() && 
          (status.equals(Status.INACTIVE) || status.equals(Status.DRAFT) )){
            throw new RuntimeException("El recurso no disponible para cliente");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("listing", listingMapper.toDto(entity));
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<Page<ListingDTO>> getAll(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) List<String> categories,
        @RequestParam(required = false) List<String> tags,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(required = false) Status status,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
         Page<Listing> filtered = null;
        if (authService.isAdmin()) {
            filtered = listingService.filter(title, categories, tags, minPrice, maxPrice, status, pageable);
        }else{
            filtered = listingService.filter(title, categories, tags, minPrice, maxPrice, Status.ACTIVE, pageable);
        }
        return ResponseEntity.ok(filtered.map(listing -> this.listingMapper.toDto(listing)));
    }

    // UPDATE
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value="/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ListingDTO> updateById(
        @PathVariable Long id,
        @RequestPart("data") UpdateListingDTO dataToEdit, // Cambiado de @RequestBody
        @RequestPart(value = "files", required = false) MultipartFile[] files) 
    {
        Listing entity = listingMapper.toEntity(dataToEdit);
        Listing saveEntity = listingService.updateById(id, entity, files);
        ListingDTO response = listingMapper.toDto(saveEntity);
        return ResponseEntity.ok(response);
    }

    // DELETE
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public  ResponseEntity<Void>  deleteById(@PathVariable Long id){
        listingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    // UPLOAD IMAGE
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/upload-single")
    public ResponseEntity<?> uploadSingle(
        @PathVariable Long id, 
        @RequestParam("file") MultipartFile file) 
    {
        try {
            String url = listingService.uploadImage(id, file);
            return ResponseEntity.ok(url);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }

    // UPLOAD IMAGES
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/upload-multiple")
    public ResponseEntity<?> uploadMultiple(
        @PathVariable Long id, 
        @RequestParam("files") MultipartFile[] files) 
    {
        try {
            List<String> urls = listingService.uploadImages(id, files);
            return ResponseEntity.ok(urls);
        } catch (Exception e) {
            return ResponseEntity
               .status(500)
               .body("Error al procesar lote: " + e.getMessage());
        }
    }


    // DELETE IMAGE
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}/images")
    public ResponseEntity<?> deleteImage(
        @PathVariable Long id,
        @RequestParam String imageUrl // El front envía la URL completa de la imagen a borrar
    ) {
        listingService.removeImageFromListing(id, imageUrl);
        return ResponseEntity.ok("Imagen eliminada correctamente");
    }


    // UPDATE STATUS
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ListingDTO> updateStatus( 
        @PathVariable Long id, 
        @RequestParam Status status) {
        Listing listing = listingService.updateStatusById(id, status);
        ListingDTO response = listingMapper.toDto(listing);
        return ResponseEntity.ok(response);
    }


}

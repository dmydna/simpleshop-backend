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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.techlab.store.dto.CreateListingDTO;
import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.ListingSummary;
import com.techlab.store.dto.UpdateListingDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.enums.Status;
import com.techlab.store.mapper.ListingMapper;
import com.techlab.store.service.AuthService;
import com.techlab.store.service.ListingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;
    private final ListingMapper listingMapper;
    private final AuthService authService;

    @Value("${app.base-url}")
    private String baseUrl;


    // CREATE: recordar que tambien crea borrador si dto.status == DRAFT
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

    // GET
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ListingDTO> getById(@PathVariable Long id){
        Listing entity = listingService.getById(id);
        ListingDTO response = listingMapper.toDto(entity);
        return ResponseEntity.ok(response);
    }

    // GET
    // NOTA: probablemente la busqueda por hash deberia ser 
    // solo publico y por id para admin
    @GetMapping("/hash/{hash}")
    public ResponseEntity<Map<String, Object>> getByHash(
        @RequestParam(required = false) Boolean fallow,
        @PathVariable String hash,
        @RequestHeader(value = "Authorization", required = false) String authHeader
    ){
        boolean isAdmin = authService.isAdmin(authHeader); 
        Listing entity = listingService.getByHash(hash);
        Status status = entity.getStatus();
        if(!isAdmin && 
          (status.equals(Status.INACTIVE) || status.equals(Status.DRAFT) )){
            throw new RuntimeException("El recurso no disponible para cliente");
        }
         Map<String, Object> response = new HashMap<>();
        
        if(status.equals(Status.DRAFT)){
            log.info("🔔 GET listing draft...");
            response.put("listing", listingMapper.toDraftDto(entity));
        }else {
            log.info("🔔 GET normal listing...");
            response.put("listing", listingMapper.toDto(entity));
            if(fallow) listingService.IncVisits(hash);
        }
         return ResponseEntity.ok(response);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<Page<ListingSummary>> getAll(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) List<String> tags,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(required = false) Status status,
        @RequestParam(required = false, defaultValue = "false") Boolean includeTags,
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        
        boolean isAdmin = authService.isAdmin(authHeader); 
        Status filterStatus = isAdmin ? status : Status.ACTIVE;
       Page<Listing> filtered = listingService.filter(title, category, tags, minPrice, maxPrice, filterStatus, pageable);
        
        if(includeTags){
            return ResponseEntity.ok(filtered.map(listing -> this.listingMapper.toSummaryFull(listing)));
        }

        return ResponseEntity.ok(filtered.map(listing -> this.listingMapper.toSummaryDto(listing)));
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
    public  ResponseEntity<?>  deleteById(@PathVariable Long id){
        listingService.deleteById(id);

        Map<String,String> responseMSG = Map.of("message", "Listing eliminado correctamente");
        return ResponseEntity.ok(responseMSG);
    }


    // UPLOAD IMAGE
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/upload-single")
    public ResponseEntity<?> uploadSingle(
        @PathVariable Long id, 
        @RequestParam("file") MultipartFile file) 
    {
        String url = listingService.addSingleImage(id, file);
        return ResponseEntity.ok(url);
    }

    // UPLOAD IMAGES
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/upload-multiple")
    public ResponseEntity<?> uploadMultiple(
        @PathVariable Long id, 
        @RequestParam("files") MultipartFile[] files) 
    {
        List<String> urls = listingService.addMultiImages(id, files);
        return ResponseEntity.ok(urls);
    }


    // DELETE IMAGE
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}/images")
    public ResponseEntity<?> deleteImage(
        @PathVariable Long id,
        @RequestParam String imageUrl // El front envía la URL completa de la imagen a borrar
    ) {
        listingService.removeImageFromListing(id, imageUrl);

        Map<String,String> responseMSG = Map.of("message", "Imagen eliminada correctamente");
        return ResponseEntity.ok(responseMSG);
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

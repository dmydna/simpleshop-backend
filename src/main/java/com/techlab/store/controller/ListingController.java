package com.techlab.store.controller;

import java.math.BigDecimal;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.techlab.store.dto.CreateListingDTO;
import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.ListingSummary;
import com.techlab.store.dto.UpdateListingDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.enums.ListingStatus;
import com.techlab.store.mapper.ListingMapper;
import com.techlab.store.service.AuthService;
import com.techlab.store.service.HashidService;
import com.techlab.store.service.ListingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/* TODO: para mejor control y flujo sobre DRAFTs, 
  considerar:
  -  implementar metodos con dto dedicados para creacion y actualizacion.
  -  implementar de ser necesario, service dedidaco.
  -  implementar de ser necesario, tabla dedicada.
*/

@Slf4j
@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;
    private final ListingMapper listingMapper;
    private final AuthService authService;
    private final HashidService hashidService;


    @Value("${app.base-url}")
    private String baseUrl;


    // CREATE: recordar que tambien crea borrador si dto.status == DRAFT
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ListingDTO> create(
        @RequestPart("data") CreateListingDTO dto,
        @RequestPart(value = "files", required = false) MultipartFile[] files
    ) {
        Listing entity = listingMapper.toEntity(dto);
        Listing saveListing = listingService.create(entity, files); 
        ListingDTO response = listingMapper.toDto(saveListing);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{hash}")
    public ResponseEntity<ListingDTO> getById(@PathVariable String hash){
        Long id = hashidService.decode(hash);
        Listing entity = listingService.getById(id);
        ListingDTO response = listingMapper.toDto(entity);
        return ResponseEntity.ok(response);
    }

    // GET
    @GetMapping("/public/{hash}")
    public ResponseEntity<Map<String, Object>> getByHash(
        @RequestParam(required = false, defaultValue = "false") Boolean fallow,
        @RequestParam(required = false, defaultValue = "4") Integer limitReviews,
        @PathVariable String hash
    ){
        Long id = hashidService.decode(hash);
        boolean isAdmin = authService.isAuthUserAdmin(); 
        Listing entity = listingService.getPublicListingById(id, limitReviews);
        ListingStatus status = entity.getStatus();
        if(!isAdmin && 
          (status.equals(ListingStatus.INACTIVE) || status.equals(ListingStatus.DRAFT) )){
            throw new AccessDeniedException("Usuario no autorizado para acceder a este recurso");
        }
         Map<String, Object> response = new HashMap<>();
        
        if(status.equals(ListingStatus.DRAFT)){
            log.info("🔔 GET listing draft...");
            response.put("listing", listingMapper.toDraftDto(entity));
        }else {
            log.info("🔔 GET normal listing...");
            response.put("listing", listingMapper.toDto(entity));
            if(fallow) listingService.IncVisits(id);
        }
         return ResponseEntity.ok(response);
    }


    // GET ALL
    @GetMapping
    public ResponseEntity<Page<ListingSummary>> getAll(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) List<String> tags,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) ListingStatus status,
        @RequestParam(required = false) String availabilityStatus,
        @RequestParam(required = false, defaultValue = "false") Boolean includeTags,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        
        boolean isAdmin = authService.isAuthUserAdmin(); 
        ListingStatus filterStatus = isAdmin ? status : ListingStatus.ACTIVE;
        Page<Listing> filtered = listingService
             .filter(title, category, tags, minPrice, maxPrice, filterStatus, availabilityStatus, pageable);
        
        if(includeTags){
            return ResponseEntity
                    .ok(filtered.map(listing -> this.listingMapper.toSummaryFull(listing)));
        }

        return ResponseEntity
                 .ok(filtered.map(listing -> this.listingMapper.toSummaryDto(listing)));
    }

    // UPDATE
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value="/{hash}", consumes = {"multipart/form-data"})
    public ResponseEntity<ListingSummary> updateById(
        @PathVariable String hash,
        @RequestPart("data") UpdateListingDTO dataToEdit, // Cambiado de @RequestBody
        @RequestPart(value = "files", required = false) MultipartFile[] files) {

        Long id = hashidService.decode(hash);
        log.info("🔔 { path: api/listings/{}, method: PUT, sku {}, stock {} }", id, dataToEdit.sku(), dataToEdit.stock());

        Listing entity = listingMapper.toEntity(dataToEdit);
        Listing saveEntity =  listingService.updateById(id, entity, files, dataToEdit.sku());
        ListingSummary response = listingMapper.toSummaryDto(saveEntity);

        return ResponseEntity.ok(response);
    }

    // DELETE
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{hash}")
    public  ResponseEntity<?>  deleteById(@PathVariable String hash){

        Long id = hashidService.decode(hash);
        listingService.deleteById(id);

        Map<String,String> responseMSG = Map.of("message", "Listing eliminado correctamente");
        return ResponseEntity.ok(responseMSG);
    }


    // UPLOAD IMAGE
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{hash}/upload-single")
    public ResponseEntity<?> uploadSingle(
        @PathVariable String hash, 
        @RequestParam("file") MultipartFile file) 
    {
        Long id = hashidService.decode(hash);
        String url = listingService.addSingleImage(id, file);
        return ResponseEntity.ok(url);
    }

    // UPLOAD IMAGES
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{hash}/upload-multiple")
    public ResponseEntity<?> uploadMultiple(
        @PathVariable String hash, 
        @RequestParam("files") MultipartFile[] files) 
    {
        Long id = hashidService.decode(hash);
        List<String> urls = listingService.addMultiImages(id, files);
        return ResponseEntity.ok(urls);
    }


    // DELETE IMAGE
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{hash}/images")
    public ResponseEntity<?> deleteImage(
        @PathVariable String hash,
        @RequestParam String imageUrl // El front envía la URL completa de la imagen a borrar
    ) {
        Long id = hashidService.decode(hash);
        listingService.removeImageFromListing(id, imageUrl);

        Map<String,String> response = Map.of("message", "Imagen eliminada correctamente");
        return ResponseEntity.ok(response);
    }


    // UPDATE STATUS
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{hash}/status")
    public ResponseEntity<ListingDTO> updateStatus( 
        @PathVariable String hash, 
        @RequestBody  Map<String, ListingStatus> request) {
        Long id = hashidService.decode(hash);
        Listing listing = listingService.updateStatusById(id, request.get("status"));
        ListingDTO response = listingMapper.toDto(listing);
        return ResponseEntity.ok(response);
    }


}

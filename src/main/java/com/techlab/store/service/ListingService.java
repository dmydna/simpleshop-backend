package com.techlab.store.service;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.enums.ListingStatus;
import com.techlab.store.enums.ReviewStatus;
import com.techlab.store.enums.Status;
import com.techlab.store.exceptions.CustomExceptions.ListingHasDeletedException;
import com.techlab.store.exceptions.CustomExceptions.ListingNotFoundException;
import com.techlab.store.exceptions.CustomExceptions.ProductNotFoundException;
import com.techlab.store.exceptions.CustomExceptions.StorageException;
import com.techlab.store.mapper.ListingMapper;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.specification.ListingSpecifications;
import com.techlab.store.utils.EnumUtils;
import com.techlab.store.utils.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@Slf4j
@RequiredArgsConstructor
@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    private final ListingMapper listingMapper;
    private final ImageService imageService;

    // -- CREATE
    // Nota. recordar que se manejan listing normal y draft en este metodo.
    // TODO: se debe chequiar en caso de publicar un listing que el producto tenga status ACTIVE.
    @Transactional
    public Listing create(Listing listing, MultipartFile[] files) {

       log.info("🔔 Creando nuevo listing...");

     // Nota. esto ya lo hace el @aftermapping en el caso de un listing normal,
     // para draft esto se omite.
     // public void publishListing(Listing listing)

        Listing saveListing = listingRepository.saveAndFlush(listing);
        // 5. Procesar imagen si el archivo no está vacío (Auxiliar)
        if(files != null){
            for (MultipartFile file : files) {
               if (!file.isEmpty()) {
                   imageFileUpload(saveListing, file);
               }
            }
        }

        return saveListing;
    }



    // -- GET BY ID
    public Listing getById(Long id){
        // BUSCA SIN RESTRICCIONES. (OCULTOS E ELIMINADOS)
        Listing listing = this.listingRepository.findById(id)
                .orElseThrow(() -> new ListingNotFoundException(id));
        if (listing.getDeletedAt() != null) {
            throw new ListingHasDeletedException(id);
        }
        return listing;
    }


    // FIXME: metodo deprecado
    // -- Visits Counter
    @Transactional
    public void IncVisits(Long hash){
        Listing listing = this.listingRepository.findActiveByHash(hash)
            .orElseThrow(() -> new ListingNotFoundException());

        Integer visits = listing.getVisits();
        listing.setVisits(visits+1);
    }


    // -- (EX) GET BY HASH
    public Listing getPublicListingById(Long id, Integer limit){

        Listing listing = this.listingRepository.findActiveById(id)
                .orElseThrow(() -> new ListingNotFoundException());
        if (listing.getDeletedAt() != null) {
            throw new ListingHasDeletedException();
        }

        Integer defaultLimit = 4;
        Integer maxLimit = 8;
        
        Integer setLimit = defaultLimit;

        if( limit != null){
            if(limit > maxLimit){
                setLimit = maxLimit;     
            }else{
                setLimit = limit;
            }
        }

        listing = limitReviews(listing, setLimit);
        return listing;
    }



    // -- GET BY HASH
    // FIXME: metodo deprecado
    public Listing getByHash(Long hash){
        Listing listing = this.listingRepository.findActiveByHash(hash)
                .orElseThrow(() -> new ListingNotFoundException());
        if (listing.getDeletedAt() != null) {
            throw new ListingHasDeletedException();
        }
        listing = limitReviews(listing, 4);
        return listing;
    }


   // TODO: Se debe devolver reviews con status ACTIVE.
    public Listing limitReviews(Listing listing, Integer limit) {
        if (listing.getProduct() != null) {
            List<Review> originalReviews = listing.getProduct().getReviews();
            
            // Filtrar y ordenar
            List<Review> filteredReviews = originalReviews.stream()
                .filter(r -> r.getStatus() == ReviewStatus.ACTIVE)
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
            
            // SOLUCIÓN: Mantener la misma referencia de lista
            originalReviews.clear(); 
            originalReviews.addAll(filteredReviews);
        }
        return listing;
    }


    public Page<Listing> findAllPage(Pageable pageable){
        return this.listingRepository.findAllByDeletedAtIsNull(pageable);
    }

    public Page<Listing> filter(
            String title,
            String category,
            List<String> tags,
            BigDecimal min, 
            BigDecimal max,
            ListingStatus status,
            String availability,
            Pageable pageable
    ) {

       log.info("🔔 Filtrando listings...");

        Specification<Listing> spec = Specification.allOf(
            ListingSpecifications.isNotDeleted(),
            ListingSpecifications.hasCategory(category),
            ListingSpecifications.hasAvailability(availability),
            ListingSpecifications.hasStatus(status),
            ListingSpecifications.hasTitle(title),
            ListingSpecifications.hasTags(tags),
            ListingSpecifications.priceInRange(min, max)
        );
        return listingRepository.findAll(spec, pageable);
    }



    @Transactional
    public Listing updateStatusById(Long id, ListingStatus newStatus) {
    log.info("🔔 Actualizando status de listing con ID {} a {}", id, newStatus);

    Listing listing = getById(id);
    ListingStatus currentStatus = listing.getStatus();

    // 1. Validar que no esté ya eliminado
    if (currentStatus == ListingStatus.DELETED) {
        throw new ListingHasDeletedException(id);
    }

    // 2. Validar la transición permitida según la jerarquía
    if (!EnumUtils.isStatusTransitionAllowed(currentStatus, newStatus)) {
        log.warn("Transición no permitida de {} a {} para listing ID {}", 
            currentStatus, newStatus, id);
        return listing;
    }

    // 3. Ejecutar lógica específica antes del cambio de estado
    if (newStatus == ListingStatus.DELETED) {
        deleteById(id); // Soft delete
    }

    // 4. Lógica específica para publicar (DRAFT -> ACTIVE)
    if (currentStatus == ListingStatus.DRAFT && newStatus == ListingStatus.ACTIVE) {
        publishListing(listing);
    }

    // 5. Actualizar estado y fecha
    listing.setStatus(newStatus);
    listing.setUpdatedAt(LocalDateTime.now());
    
    return listing; 
    }


    // Nota. esto ya lo hace el @aftermapping en el caso de un listing normal.
    public void publishListing(Listing listing){
        log.info("🔔 publicando listing draft con ID {}...", listing.getId());
        // validamos existencia de producto
        Product existingProduct =  productRepository
            .findActiveBySku(listing.getProduct().getSku())
            .orElseThrow(() -> new ProductNotFoundException());
        listing.setProduct(existingProduct);
        listing.getProduct().setStatus(Status.ACTIVE);
        listing.setStatus(ListingStatus.ACTIVE);
        listing.setAvailabilityStatus("In Stock");
    }




    // CHECKME actualiza imagenes de lista.
    @Transactional
    public Listing updateById(
        Long id, 
        Listing dataToEdit, 
        MultipartFile[] files, 
        String sku
    ) {

        log.info("🔔 actualizando listing con ID {}...", id);

        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new ListingNotFoundException(dataToEdit.getId()));
        
        ListingStatus status = listing.getStatus();

        // Logica de status == DELETED
        if(status.equals(ListingStatus.DELETED)){
            // Si esta eliminado no puede actualizarse.
            throw new ListingHasDeletedException(dataToEdit.getId());
        }

        // Logica de status == DRAFT
        if(status.equals(ListingStatus.DRAFT)){
            log.info("🔔 actualizando listing-draft con ID {}, status {}, sku {}", id, status, sku);
            // Nota: solo draft puede actualizar producto (por SKU)
            if(sku != null) updateProductBySku(id, sku);
        }

        // Actualizar status
        if(dataToEdit.getStatus() != null){ 
            updateStatusById(id, dataToEdit.getStatus());
        }

        // Importante: esta funcion requiere listing.images sin modificar.
        updateImages(id, dataToEdit.getImages(), files);

        listing.setUpdatedAt(LocalDateTime.now());

        return listingMapper.updateFromEntity(dataToEdit, listing);
    }



    @Transactional
    public Listing updateProductBySku(Long id, String sku){
        log.info("🔔 actualizando producto de listing-draft con ID {}...", id);

        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new ListingNotFoundException(id));

        Product existingProduct =  productRepository
                    .findActiveBySku(sku)
                    .orElseThrow(() -> new ProductNotFoundException());

        listing.setProduct(existingProduct);

        return listing;
    }


    public List<String> updateImages(
            Long id, 
            List<String> updatedImages, 
            MultipartFile[] files
        ){

        log.info("🔔 Actualizando imagenes de listing con ID {}...", id);
        Listing listing = this.listingRepository.findActiveById(id)
            .orElseThrow(() -> new ListingNotFoundException(id));
        List<String> currentImages = listing.getImages();
        // Busco las imágenes que ya no están en el nuevo DTO
        List<String> deletedImages = 
            currentImages
                .stream()
                .filter(e -> !updatedImages.contains(e))
                .collect(Collectors.toList());

        // Borrar las imagenes eliminadas
        deletedImages
           .forEach(imageName -> removeImageFromListing(id, imageName));
        // Subo las nuevas imagenes.
        if (files != null && files.length > 0) {  addMultiImages(id, files); }

        return listing.getImages();
    }


    public void deleteById(Long id) {
        log.info("🔔 Eliminando listing con ID {}...", id);
        Listing listing = this.listingRepository.findActiveById(id)
           .orElseThrow(() -> new ListingNotFoundException(id));

        // Elimina reviews con status "PENDING".
        listingRepository.deleteReviewByListingIdAndStatus(id, ReviewStatus.PENDING);
        // Borrar Imagenes del Storage
        // NOTA: NO eliminar thumbnail
        listing.getImages().forEach(fileStorageService::deleteFile);
        listing.setStatus(ListingStatus.DELETED);
        listing.setDeletedAt(LocalDateTime.now());
        listingRepository.save(listing);
    }


    public boolean isDeleted(Long id){
        Listing entity = listingRepository.findById(id)
                .orElseThrow(() -> new ListingNotFoundException(id));
        return entity.getDeletedAt() != null;
    }

// --- MÉTODOS AUXILIARES PRIVADOS ---

    @Transactional
    public void removeImageFromListing(Long listingId, String imageUrl) {
        log.info("🔔 Eliminado imagen {} de listing con ID {}...",imageUrl, listingId);
        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new ListingNotFoundException(listingId));
        boolean removed = listing.getImages().remove(imageUrl);
        if (removed) {
            // 2. Si se quitó de la DB con éxito, procedemos a borrar el archivo físico
            fileStorageService.deleteFile(imageUrl);
            listingRepository.save(listing);
        }
    }


    @Transactional
    public String addSingleImage(Long id, MultipartFile file) {
        log.info("🔔 Subiendo imagen para listing...");
        if (file.isEmpty()) throw new StorageException("⚠️ El archivo está vacío");
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new ListingNotFoundException(id));
        return imageFileUpload(listing, file);
    }

    @Transactional
    public List<String> addMultiImages(Long id, MultipartFile[] files) {
        log.info("🔔 Subiendo lista de imagenes para listing...");
        if (files == null || files.length == 0)
            throw new StorageException("⚠️ El archivo está vacío");
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ListingNotFoundException(id));
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                urls.add(imageFileUpload(listing, file));
            }
        }
        return urls;
    }


    private String createThumbnail(MultipartFile file){
            String thumbnailUrl = null;
            try {
                thumbnailUrl = imageService.generateAndSaveThumbnail(file);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return thumbnailUrl;
    }


    private String imageFileUpload(Listing listing, MultipartFile file) {
        log.info("🔔 Subiendo imagen para listing...");
        String finalUrl = fileStorageService.storeFile(file, listing.getId());
        listing.getImages().add(finalUrl);

        if (listing.getThumbnail() == null ||
            listing.getThumbnail().isEmpty()) {
            listing.setThumbnail(createThumbnail(file));
        }
        listingRepository.save(listing);
        return finalUrl;
    }

}





package com.techlab.store.service;


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

import com.techlab.store.dto.ListingDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
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
import com.techlab.store.utils.HashUtil;
import com.techlab.store.utils.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@Slf4j
@RequiredArgsConstructor
@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final StringUtils stringUtils;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    private final ListingMapper listingMapper;

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


    // -- Visits Counter
    @Transactional
    public void IncVisits(String hash){
        Listing listing = this.listingRepository.findActiveByHash(hash)
            .orElseThrow(() -> new ListingNotFoundException());

        Integer visits = listing.getVisits();
        listing.setVisits(visits+1);
    }



    // -- GET BY HASH
    public Listing getByHash(String hash){
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
            Double min, Double max,
            Status status,
            Pageable pageable
    ) {

       log.info("🔔 Filtrando listings...");

        Specification<Listing> spec = Specification
            .where(ListingSpecifications.isNotDeleted())
            .and(ListingSpecifications.hasCategory(category))
            .and(ListingSpecifications.hasStatus(status))
            .and(ListingSpecifications.hasTitle(title))
            .and(ListingSpecifications.hasTags(tags))
            .and(ListingSpecifications.priceInRange(min, max));

        return listingRepository.findAll(spec, pageable);
    }


    public Page<ListingDTO> findByFilter(
            String title,
            String category,
            List<String> tags,
            Double min, Double max,
            Status status,
            Pageable pageable
    ) {
        // 3. Convertir a Page de DTOs usando tu mapper
        return filter(title, category, tags, min, max, status, pageable)
            .map(listing -> this.listingMapper.toDto(listing));
    }


    @Transactional
    public Listing updateStatusById(Long id, Status status){
        log.info("🔔 actualizando status de listing con ID {}...", id);
        Listing listing = getById(id);

        if(isDeleted(id)){ 
           throw new ListingHasDeletedException(id) ;
        }

        if(status.equals(Status.DELETED)){ 
            deleteById(id); 
        }

        // validamos antes de "crear" el listing a partir del borrador.
        if(listing.getStatus().equals(Status.DRAFT) && 
           status.equals(Status.ACTIVE)){
             publishListing(listing);
        }

        listing.setStatus(status);
        listing.setUpdatedAt(LocalDateTime.now());
        return listing;
    }


    // Nota. esto ya lo hace el @aftermapping en el caso de un listing normal.
    public void publishListing(Listing listing){
        log.info("🔔 publicando listing draft con ID {}...", listing.getId());
        // validamos existencia de producto
       Product existingProduct =  productRepository
                   .findBySku(listing.getProduct().getSku())
                   .orElseThrow(() -> new ProductNotFoundException());
        listing.setProduct(existingProduct);
        listing.getProduct().setStatus(Status.ACTIVE);
        listing.setStatus(Status.ACTIVE);
        listing.setHash(HashUtil.generateShortHash());
        listing.setAvailabilityStatus("In Stock");
    }


    // CHECKME actualiza imagenes de lista.
    @Transactional
    public Listing updateById(Long id, Listing dataToEdit, MultipartFile[] files) {

        log.info("🔔 actualizando listing con ID {}...", id);

        Listing listing = listingRepository.findActiveById(id)
                .orElseThrow(() -> new ListingNotFoundException(dataToEdit.getId()));
        
        if(dataToEdit.getStatus() != null){ updateStatusById(id, dataToEdit.getStatus());}
        // Importante: esta funcion requiere listing.images sin modificar.
        updateImages(id, dataToEdit.getImages(), files);

        listing.setUpdatedAt(LocalDateTime.now());

        return listingMapper.updateFromEntity(dataToEdit, listing);
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
        listing.getImages().forEach(fileStorageService::deleteFile);
        listing.setStatus(Status.DELETED);
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

    private String imageFileUpload(Listing listing, MultipartFile file) {
        log.info("🔔 Subiendo imagen para listing...");
        String finalUrl = fileStorageService.storeFile(file, listing.getId());
        listing.getImages().add(finalUrl);

        if (listing.getThumbnail() == null ||
            listing.getThumbnail().isEmpty()) {
            listing.setThumbnail(finalUrl);
        }
        listingRepository.save(listing);
        return finalUrl;
    }

}





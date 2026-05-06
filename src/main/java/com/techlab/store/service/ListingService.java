package com.techlab.store.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.techlab.store.dto.ListingDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.enums.Status;
import com.techlab.store.mapper.ListingMapper;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.specification.ListingSpecifications;
import com.techlab.store.utils.HashUtil;
import com.techlab.store.utils.StringUtils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.techlab.store.exceptions.CustomExceptions.*;

import jakarta.persistence.metamodel.IdentifiableType;


@Service
@Slf4j
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;
    private final StringUtils stringUtils;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    private final ListingMapper listingMapper;

    // -- CREATE
    @Transactional
    public Listing create(Listing listing, MultipartFile[] files) {

        // buscamos existencia de producto
        Product product =  this.productRepository
            .findBySku(listing.getProduct().getSku())
            .orElseThrow(() -> new ProductNotFoundException());

        Listing saveListing = listingRepository.saveAndFlush(listing);
        // 5. Procesar imagen si el archivo no está vacío (Auxiliar)
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                handleImageUpload(saveListing, file);
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

   public Listing limitReviews(Listing listing, Integer limit) {
         listing.getProduct().getReviews().stream()
             .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
             .limit(limit)
             .collect(Collectors.toList());
    return listing; // Retorna el listing modificado
   }


    public Page<Listing> findAllPage(Pageable pageable){
        return this.listingRepository.findAllByDeletedAtIsNull(pageable);
    }

    public Page<Listing> filter(
            String title,
            List<String> categories,
            List<String> tags,
            Double min, Double max,
            Status status,
            Pageable pageable
    ) {
        Specification<Listing> spec = Specification
            .where(ListingSpecifications.isNotDeleted())
            .and(ListingSpecifications.hasCategories(categories))
            .and(ListingSpecifications.hasStatus(status))
            .and(ListingSpecifications.hasTitle(title))
            .and(ListingSpecifications.hasTags(tags))
            .and(ListingSpecifications.priceInRange(min, max));

        return listingRepository.findAll(spec, pageable);
    }


    public Page<ListingDTO> findByFilter(
            String title,
            List<String> categories,
            List<String> tags,
            Double min, Double max,
            Status status,
            Pageable pageable
    ) {
        // 3. Convertir a Page de DTOs usando tu mapper
        return filter(title, categories, tags, min, max, status, pageable)
            .map(listing -> this.listingMapper.toDto(listing));
    }


    @Transactional
    public Listing updateStatusById(Long id, Status status){
        log.info("🔔 actualizando status de listing con ID {}...", id);
        Listing listing = this.listingRepository.findById(id)
              .orElseThrow(() -> new ListingNotFoundException(id));

        if(isDeleted(id)){ throw new ListingHasDeletedException(id) ;}

        if(status.equals(Status.DELETED)){ deleteById(id); }

        listing.setStatus(status);
        return listing;
    }


    @Transactional
    public Listing updateById(Long id, Listing dataToEdit, MultipartFile[] files) {

        log.info("🔔 actualizando listing con ID {}...", id);

        Listing listing = listingRepository.findActiveById(id)
                .orElseThrow(() -> new ListingNotFoundException(dataToEdit.getId()));
        
        if(dataToEdit.getStatus() != null){ updateStatusById(id, dataToEdit.getStatus());}
        // Importante: esta funcion requiere listing.images sin modificar.
        if(files != null && files.length != 0){ 
            updateImages(id, dataToEdit.getImages(), files);}

        return listingMapper.updateFromEntity(dataToEdit, listing);
    }



    public List<String> updateImages(
            Long id, 
            List<String> updatedImages, 
            MultipartFile[] files){
        log.info("🔔 actualizando imagenes de listing con ID {}...", id);
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
        if (files != null && files.length > 0) {  uploadImages(id, files); }

        return listing.getImages();
    }


    public void deleteById(Long id) {
        log.info("🔔 eliminando listing con ID {}...", id);
        Listing listing = this.listingRepository.findActiveById(id)
           .orElseThrow(() -> new ListingNotFoundException(id));
        //this.productRepository.delete(post);
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
        log.info("🔔 eliminado imagen {} de listing con ID {}...",imageUrl, listingId);
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
    public String uploadImage(Long id, MultipartFile file) {
        log.info("subiendo imagen para listing...");
        if (file.isEmpty()) throw new StorageException("❌ El archivo está vacío");
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new ListingNotFoundException(id));
        return handleImageUpload(listing, file);
    }

    @Transactional
    public List<String> uploadImages(Long id, MultipartFile[] files) {
        log.info("subiendo imagenes para listing...");
        if (files == null || files.length == 0)
            throw new StorageException("❌ El archivo está vacío");
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ListingNotFoundException(id));
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                urls.add(handleImageUpload(listing, file));
            }
        }
        return urls;
    }

    private String handleImageUpload(Listing listing, MultipartFile file) {
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





package com.techlab.store.service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.techlab.store.dto.ListingDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.enums.Visibility;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.utils.HashUtil;
import com.techlab.store.mapper.ListingMapper;
import com.techlab.store.utils.StringUtils;

import org.springframework.data.jpa.domain.Specification;

import com.techlab.store.specification.ListingSpecifications;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;
    private final StringUtils stringUtils;
    private final ProductRepository productRepository;
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ListingMapper listingMapper;

    @Transactional
    public ListingDTO create(ListingDTO dto, MultipartFile[] files) {
        Listing newListing = createListingFromDto(dto);
        newListing = listingRepository.saveAndFlush(newListing);
        // 5. Procesar imagen si el archivo no está vacío (Auxiliar)
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                handleImageUpload(newListing, file);
            }
        }
        return listingMapper.toDto(newListing);
    }




    // -- GET BY ID
    public ListingDTO getByIdAdmin(Long id){
        // BUSCA SIN RESTRICCIONES. (OCULTOS E ELIMINADOS)
        Listing listing = this.listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
        if (listing.getDeletedDate() != null) {
            throw new RuntimeException("El recurso ha sido eliminado");
        }
        return this.listingMapper.toDto(listing);
    }

    // -- GET BY HASH
    public ListingDTO getByHash(String hash){
        Listing listing = this.listingRepository.findActiveByHash(hash)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
        if (listing.getDeletedDate() != null) {
            throw new RuntimeException("El recurso ha sido eliminado");
        }
        return this.listingMapper.toDto(listing);
    }


    public Page<Listing> findAllPage(Pageable pageable){
        return this.listingRepository.findAllByDeletedDateIsNull(pageable);
    }


    public Page<ListingDTO> filter(
            String title,
            List<String> categories,
            List<String> tags,
            Double min, Double max,
            Visibility visibility,
            Pageable pageable
    ) {

        Specification<Listing> spec = ListingSpecifications.isNotDeleted();

        if (!CollectionUtils.isEmpty(categories)) {
            spec = spec.and(ListingSpecifications.hasCategories(categories));
        }

        if(visibility != null){
            spec = spec.and(ListingSpecifications.hasVisibilty(visibility));
        }

        if (stringUtils.hasText(title)) {
            spec = spec.and(ListingSpecifications.hasTitle(title));
        }

        if (!CollectionUtils.isEmpty(tags)) {
            spec = spec.and(ListingSpecifications.hasTags(tags));
        }

        spec = spec.and(ListingSpecifications.priceInRange(min, max));
        Page<Listing> listingsPage = listingRepository.findAll(spec, pageable);
        // 3. Convertir a Page de DTOs usando tu mapper
        return listingsPage.map(listing -> this.listingMapper.toDto(listing));
    }





    public ListingDTO updateById(Long id, ListingDTO dataToEdit, MultipartFile[] files) {
        Listing listing = this.listingRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Error al actualizar: No se encontro listing con id "+ id));
        // Importante: este mapper ignora el campo images.
        listingMapper.updateFromDto(dataToEdit, listing);
        // Importante: esta funcion requiere listing.images sin modificar.
        if(files != null && files.length != 0){
            updateImages(id, dataToEdit.images(), files);
        }

        Listing saveListing = this.listingRepository.save(listing);
        return listingMapper.toDto(saveListing);
    }



    public List<String> updateImages(
            Long id, List<String> updatedImages, MultipartFile[] files){
        Listing listing = this.listingRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));

        List<String> currentImages = listingMapper.toDto(listing).images();
        // Busco las imágenes que ya no están en el nuevo DTO
        List<String> deletedImages = currentImages.stream()
                .filter(e -> !updatedImages.contains(e))
                .collect(Collectors.toList());

        // Borrar las imagenes eliminadas
        deletedImages.forEach(imageName -> removeImageFromListing(id, imageName));
        // Subo las nuevas imagenes.
        if (files != null && files.length > 0) {
             uploadImages(id, files);
        }
        return listing.getImages();
    }

    public ListingDTO changeVisibility(Long id, Visibility visibility){
        Listing listing = this.listingRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
        listing.setVisibility(visibility);
        Listing saveListing = this.listingRepository.save(listing);
        return this.listingMapper.toDto(saveListing);
    }



    public ListingDTO deleteById(Long id) {
        Listing listing = this.listingRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
        //this.productRepository.delete(post);
        // Borrar Imagenes del Storage
        listing.getImages().forEach(fileStorageService::deleteFile);
        listing.setDeleted(true);
        listing.setDeletedDate(LocalDate.now());
        Listing saveListing = this.listingRepository.save(listing);
        return this.listingMapper.toDto(saveListing);
    }



    @Transactional
    public List<ListingDTO> saveAll(List<ListingDTO> dtos) {
        log.info("\n\n >> Iniciando persistencia masiva de {} elementos \n\n", dtos.size());
        // 1. Convertimos los DTOs a Entidades preparadas
        List<Listing> listingsToSave = dtos.stream().map(dto-> createListingFromDto(dto)
        ).collect(Collectors.toList());
        // 2. Guardamos todos los Listings de una vez
        List<Listing> savedListings = listingRepository.saveAll(listingsToSave);
        // 3. Retornamos la lista convertida a DTO para el Frontend
        return savedListings.stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }


// --- MÉTODOS AUXILIARES PRIVADOS ---

    @Transactional
    public void removeImageFromListing(Long listingId, String imageUrl) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing no encontrado"));
        // 1. Eliminar la URL de la lista en la base de datos
        boolean removed = listing.getImages().remove(imageUrl);
        if (removed) {
            // 2. Si se quitó de la DB con éxito, procedemos a borrar el archivo físico
            fileStorageService.deleteFile(imageUrl);
            listingRepository.save(listing);
        }
    }


    @Transactional
    public String uploadImage(Long id, MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("El archivo está vacío");
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Listing no encontrado"));
        return handleImageUpload(listing, file);
    }

    @Transactional
    public List<String> uploadImages(Long id, MultipartFile[] files) {
        if (files == null || files.length == 0)
            throw new IllegalArgumentException("No hay archivos");
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Listing no encontrado"));
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


    // BUSQUEDA SEGURA (NO MUESTRA OCULTOS Y ELIMINADOS)
    public ListingDTO getById(Long id) {
        Specification<Listing> spec = ListingSpecifications.isNotDeleted()
                .and(ListingSpecifications.hasId(id));
        Listing listing = listingRepository.findOne(spec)
                .orElseThrow(() -> new EntityNotFoundException("Listing no encontrado"));
        return this.listingMapper.toDto(listing);
    }


    public Listing createListingFromDto(ListingDTO dto){
        Listing listing = listingMapper.toEntity(dto);
        Optional<Product> existingProduct = productRepository.findBySku(dto.sku());

        if (existingProduct.isPresent()) {
            // Si existe, usamos el producto de la DB en lugar del que creó MapStruct
            listing.setProduct(existingProduct.get());
        }
        listing.setCreatedDate(LocalDate.now());
        listing.setHash(HashUtil.generateShortHash());
        listing.setVisibility(Visibility.PUBLIC);

        return listing;
    }



}





package com.techlab.store.service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.techlab.store.dto.ListingDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.enums.Visibility;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.utils.HashUtil;
import com.techlab.store.utils.ListingMapper;
import com.techlab.store.utils.StringUtils;

import org.springframework.data.jpa.domain.Specification;

import com.techlab.store.specification.ListingSpecifications;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ListingService {

    private final ListingRepository listingRepository;
    private final StringUtils stringUtils;
    private final ProductRepository productRepository;
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ListingMapper listingMapper;

    public ListingService(ListingRepository listingRepository, StringUtils stringUtils, ProductRepository productRepository) {
        this.listingRepository = listingRepository;
        this.stringUtils = stringUtils;
        this.productRepository = productRepository;
    }



    @Transactional
    public ListingDTO create(ListingDTO dto, MultipartFile[] files) {
        // 1. Obtener o crear el producto (Auxiliar)
        Product product = getOrCreateProduct(dto);
        // 2. Mapear DTO a Entidad base
        Listing newListing = listingMapper.toEntity(dto);
        newListing.setProduct(product);
        // 3. Procesar Reviews si existen (Auxiliar)
        processReviews(dto, newListing); 
        newListing.setHash(HashUtil.generateShortHash());
        newListing.setVisibility(Visibility.PUBLIC);
        // 4. Guardar primero para obtener el ID (necesario para el nombre del archivo)
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
    public ListingDTO getById(Long id){
        Listing listing = this.listingRepository.findActiveById(id)
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

    public Page<ListingDTO> search(String title, List<String> categories, List<String> tags, Double min, Double max, Pageable pageable) {
        Specification<Listing> spec = Specification.where((root, query, cb) -> cb.isNull(root.get("deletedDate")));
    
        if (categories != null && !categories.isEmpty()) {
            spec = spec.and(ListingSpecifications.hasCategories(categories));
        }

        if (title != null && !title.isEmpty()) {
            spec = spec.and(ListingSpecifications.hasTitle(title));
        }
        
        if (tags != null && !tags.isEmpty()) {
            spec = spec.and(ListingSpecifications.hasTags(tags));
        }
    
        spec = spec.and(ListingSpecifications.priceInRange(min, max));
        Page<Listing> listingsPage = listingRepository.findAll(spec, pageable);
        // 3. Convertir a Page de DTOs usando tu mapper
        return listingsPage.map(listing -> this.listingMapper.toDto(listing));
    }


    public ListingDTO updateById(Long id, Listing dataToEdit) {
        Listing listing = this.listingRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));

        if (!stringUtils.isEmpty(dataToEdit.getTitle())){
            System.out.printf("Editando el nombre del producto: viejo:%s - nuevo:%s", listing.getTitle(), dataToEdit.getTitle());
            listing.setTitle(dataToEdit.getTitle());
        }

        if (!stringUtils.isEmpty(dataToEdit.getDescription()))
            listing.setDescription(dataToEdit.getDescription());

        if (null != dataToEdit.getDeleted())
            listing.setDeleted(dataToEdit.getDeleted());

        listing.setPrice(dataToEdit.getPrice());
        listing.setDiscountPercentage(dataToEdit.getDiscountPercentage());
        listing.setRating(dataToEdit.getRating());
        listing.setWarrantyInformation(dataToEdit.getWarrantyInformation());
        listing.setShippingInformation(dataToEdit.getShippingInformation());
        listing.setAvailabilityStatus(dataToEdit.getAvailabilityStatus());
        listing.setReturnPolicy(dataToEdit.getReturnPolicy());
        listing.setMinimumOrderQuantity(dataToEdit.getMinimumOrderQuantity());
        
        if(null != dataToEdit.getImages()) 
            listing.setImages(dataToEdit.getImages());

        if(null != dataToEdit.getThumbnail())
            listing.setThumbnail(dataToEdit.getThumbnail());

        Listing saveListing = this.listingRepository.save(listing);
        return this.listingMapper.toDto(saveListing);
    }


    public ListingDTO changeVisibility(Long id, Visibility visibility){
        Listing listing = this.listingRepository.findActiveById(id)
        .orElseThrow(() -> new RuntimeException("No encontrado"));
        listing.setVisibility(visibility);
        return this.listingMapper.toDto(listing);
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
        log.info("Iniciando persistencia masiva de {} elementos", dtos.size());
        // 1. Convertimos los DTOs a Entidades preparadas
        List<Listing> listingsToSave = dtos.stream().map(dto -> {
            // Creamos el Producto (Hijo)
            Product product = createNewProductFromDto(dto);
            // Es vital guardar el producto primero si no usas CascadeType.PERSIST
            product = productRepository.save(product);
            // Mapeamos el Listing (Padre)
            Listing listing = this.listingMapper.toEntity(dto);
            listing.setProduct(product); // Establecemos la relación
            // Manejamos las Reviews si existen
            if (dto.getReviews() != null && !dto.getReviews().isEmpty()) {
                Listing finalListing = listing;
                Set<Review> reviews = dto.getReviews().stream()
                        .map(rNode -> ReviewFromDto(rNode, finalListing))
                        .collect(Collectors.toSet());
                listing.setReviews(reviews);
                listing.setHash(HashUtil.generateShortHash());
                listing.setVisibility(Visibility.PUBLIC);
            }
            return listing;
        }).collect(Collectors.toList());
        // 2. Guardamos todos los Listings de una vez
        List<Listing> savedListings = listingRepository.saveAll(listingsToSave);
        // 3. Retornamos la lista convertida a DTO para el Frontend
        return savedListings.stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }


// --- MÉTODOS AUXILIARES PRIVADOS ---

    public Product createNewProductFromDto(ListingDTO dto){
        Product product = new Product();
        if(null == dto.getProduct_name()){
            product.setName(dto.getTitle());
        }else {
            product.setName(dto.getProduct_name());
        }
        product.setSku(dto.getSku());
        product.setBrand(dto.getBrand());
        product.setWeight(dto.getWeight());
        product.setDimensions(dto.getDimensions());
        product.setStock(dto.getStock());
        product.setCategory(dto.getCategory());
        product.setTags(dto.getTags());
        product.setMeta(dto.getMeta());
        return product;
    }

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


    private Product getOrCreateProduct(ListingDTO dto) {
        if (dto.getProduct_id() != null) {
            return productRepository.findById(dto.getProduct_id())
                    .orElseGet(() -> createNewProductFromDto(dto));
        }
        if (dto.getSku() != null) {
            return productRepository.findBySku(dto.getSku())
                    .orElseGet(() -> createNewProductFromDto(dto));
        }
        return createNewProductFromDto(dto);
    }

    private void processReviews(ListingDTO dto, Listing listing) {
        if (dto.getReviews() != null && !dto.getReviews().isEmpty()) {
            Set<Review> reviews = dto.getReviews().stream()
                    .map(rNode -> ReviewFromDto(rNode, listing))
                    .collect(Collectors.toSet());
            listing.setReviews(reviews);
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
        if (files == null || files.length == 0) throw new IllegalArgumentException("No hay archivos");

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

        if (listing.getThumbnail() == null || listing.getThumbnail().isEmpty()) {
            listing.setThumbnail(finalUrl);
        }
        listingRepository.save(listing);
        return finalUrl;
    }

    public Review ReviewFromDto(Review rNode, Listing parent) {
        Review r = new Review();
        r.setRating(rNode.getRating());
        r.setComment(rNode.getComment());
        r.setReviewerName(rNode.getReviewerName());
        r.setListing(parent);
        return r;
    }

    
}

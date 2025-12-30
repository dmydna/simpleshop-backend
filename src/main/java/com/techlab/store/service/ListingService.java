package com.techlab.store.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.techlab.store.dto.ListingDTO;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.utils.ListingMapper;
import com.techlab.store.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ListingService {

    private final ListingRepository listingRepository;
    private final StringUtils stringUtils;
    private final ProductRepository productRepository;

    @Autowired
    private ListingMapper listingMapper;

    public ListingService(ListingRepository listingRepository, StringUtils stringUtils, ProductRepository productRepository) {
        this.listingRepository = listingRepository;
        this.stringUtils = stringUtils;
        this.productRepository = productRepository;
    }

    public Review ReviewFromDto(Review rNode, Listing parent) {
        Review r = new Review();
        r.setRating(rNode.getRating());
        r.setComment(rNode.getComment());
        r.setReviewerName(rNode.getReviewerName());
        r.setListing(parent);
        return r;
    }

    @Transactional
    public ListingDTO createListing(ListingDTO dto){
        Product product;
        if (dto.getProduct_id() != null) {
            // Intentamos buscarlo si el DTO trae ID
            product = productRepository.findById(dto.getProduct_id())
                    .orElseGet(() -> createNewProductFromDto(dto));
        } else {
            // Si no trae ID, creamos uno nuevo directamente
            product = createNewProductFromDto(dto);
        }
        product = productRepository.saveAndFlush(product);
        // 2. Mapear el Listing a partir del DTO
        Listing newListing = this.listingMapper.toEntity(dto);

        // Vinculación vital: asignar el producto (ya persistido o recuperado) al listing
        newListing.setProduct(product);

        // 3. Procesar las Reviews (Relación bidireccional)
        if (dto.getReviews() != null) {
            Set<Review> reviews = dto.getReviews().stream()
                    .map(rNode -> ReviewFromDto(rNode, newListing))
                    .collect(Collectors.toSet());
            newListing.setReviews(reviews);
        }

        // 4. Guardar y retornar
        Listing savedListing = this.listingRepository.save(newListing);
        return listingMapper.toDto(savedListing);
    }


    public ListingDTO getListingById(Long id){
        Listing listing = this.listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
        return this.listingMapper.toDto(listing);
    }

    public List<ListingDTO> findAllListing(){
        List<Listing> listings = this.listingRepository.findAll();
        return this.listingMapper.toDtoList(listings);
    }

    public ListingDTO editListingById(Long id, Listing dataToEdit) {
        Listing listing = this.listingRepository.findById(id)
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
        listing.setReviews(dataToEdit.getReviews());
        listing.setReturnPolicy(dataToEdit.getReturnPolicy());
        listing.setMinimumOrderQuantity(dataToEdit.getMinimumOrderQuantity());
        listing.setImages(dataToEdit.getImages());
        listing.setThumbnail(dataToEdit.getThumbnail());

        Listing saveListing = this.listingRepository.save(listing);
        return this.listingMapper.toDto(saveListing);
    }



    public ListingDTO deleteListingById(Long id) {
        Listing listing = this.listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));

        //this.productRepository.delete(post);
        listing.setDeleted(true);
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
//    @Transactional
//    public List<ListingDTO> saveAll(List<ListingDTO> listings) {
//        for (ListingDTO listing : listings) {
//            if (listing.getReviews() != null) {
//                listing.getReviews().forEach(review ->
//                        review.setListing(this.listingMapper.toEntity(listing)));
//            }
//        }
//
//        List<Listing> entityList = this.listingMapper.toEntityList(listings);
//        List<Listing> saveListings = listingRepository.saveAll(entityList);
//        return  this.listingMapper.toDtoList(saveListings);
//    }
}

package com.techlab.store.mapper;

import com.techlab.store.dto.CreateListingDTO;
import com.techlab.store.dto.UpdateListingDTO;
import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.ListingSummary;
import com.techlab.store.dto.ListingDraftDTO;
import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.enums.Status;
import com.techlab.store.utils.EnumUtils;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.techlab.store.exceptions.CustomExceptions.ProductNotFoundException;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.utils.HashUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ListingMapper {

    @Autowired
    ProductRepository productRepository;


    @Mapping(target = "sku", expression = "java(listing.getProduct() != null ? listing.getProduct().getSku() : null)")
    @Mapping(source = "createdAt", target = "meta.createdAt")
    @Mapping(source = "updatedAt", target = "meta.updatedAt")
    @Mapping(source = "deletedAt", target = "meta.deletedAt")
    @Mapping(source = "status", target = "meta.status", qualifiedByName = "statusToString")
    public abstract ListingDraftDTO toDraftDto(Listing listing);




    @Mapping(source = "product.dimensions.width",  target = "dimensions.width") 
    @Mapping(source = "product.dimensions.height", target = "dimensions.height") 
    @Mapping(source = "product.dimensions.depth",  target = "dimensions.depth") 
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.brand", target = "brand")
    @Mapping(source = "product.sku", target = "sku")
    @Mapping(source = "product.tags", target = "tags")
    @Mapping(source = "product.rating", target = "rating")
    @Mapping(source = "product.reviews", target = "reviews")
    @Mapping(source = "product.category", target = "category")
    @Mapping(source = "product.weight", target = "weight")
    @Mapping(source = "createdAt", target = "meta.createdAt")
    @Mapping(source = "updatedAt", target = "meta.updatedAt")
    @Mapping(source = "deletedAt", target = "meta.deletedAt")
    @Mapping(source = "status", target = "meta.status", qualifiedByName = "statusToString")
    public abstract ListingDTO toDto(Listing listing);


    @Mapping(source = "createdAt", target = "meta.createdAt")
    @Mapping(source = "updatedAt", target = "meta.updatedAt")
    @Mapping(source = "deletedAt", target = "meta.deletedAt")
    @Mapping(source = "status",    target = "meta.status", qualifiedByName = "statusToString")
    public abstract ListingSummary toSummaryDto(Listing listing);

    @Mapping(source = "product.tags", target = "tags")
    @Mapping(source = "createdAt", target = "meta.createdAt")
    @Mapping(source = "updatedAt", target = "meta.updatedAt")
    @Mapping(source = "deletedAt", target = "meta.deletedAt")
    @Mapping(source = "status",    target = "meta.status", qualifiedByName = "statusToString")
    public abstract ListingSummary toSummaryFull(Listing listing);


    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "comment", target = "comment")
    @Mapping(source = "rating", target = "rating")
    public abstract ReviewDTO ReviewToDto(Review entity);

    @InheritInverseConfiguration(name = "toDto") 
    @Mapping(source = "meta.status", target = "status", qualifiedByName = "stringToStatus")
    @Mapping(source = "dto", target = "product")
    public abstract Listing toEntity(ListingDTO dto);

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "productName", target = "name")
    @Mapping(source = "sku", target = "sku")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "brand", target = "brand")
    @Mapping(target = "meta", ignore = true)
    public abstract Product productFromDto(ListingDTO dto);

    @Mapping(target = "productId", source = "id") // <--
    @Mapping(target = "productName", source = "name")
    public abstract ListingDTO productToDto(Product product);

    @Mapping(target = "product", ignore = true) // Lo asignamos manualmente en el AfterMapping
    public abstract Review toReviewEntity(ReviewDTO reviewDto, @Context Product parent);

    @AfterMapping
    protected void linkReviewToParent(
            ReviewDTO reviewDto,
            @MappingTarget Review review,
            @Context Product parent) {
        review.setProduct(parent);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true)
    public abstract Listing updateFromDto(ListingDTO dto, @MappingTarget Listing listing);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    public abstract Listing updateFromEntity(
        Listing update, 
        @MappingTarget Listing listing);

    @InheritInverseConfiguration(name = "toDto") 
    @Mapping(target = "id", ignore = true)
    public abstract Listing toEntity(CreateListingDTO dto);

    @Mapping(target = "status", ignore = true)
    public abstract Listing toEntity(UpdateListingDTO dto);


    @AfterMapping
    protected void listingAfterMapping(
        @MappingTarget Listing listing, 
        CreateListingDTO dto) 
    {
       // Saltamos validaciones y campos no necesarios para borrador
       // Nota: despues lo agregamos antes de publicar.
       if(dto.status() != null && dto.status().equals(Status.DRAFT)){
         log.info("🔔 Creando listing draft...");
         listing.setAvailabilityStatus("Pending");
         listing.setProduct(null);
         listing.setHash(HashUtil.generateShortHash());
         return;
       }
         Product existingProduct =  productRepository
                   .findBySku(dto.sku())
                   .orElseThrow(() -> new ProductNotFoundException());
         listing.setProduct(existingProduct);
         listing.getProduct().setStatus(Status.ACTIVE);
         listing.setStatus(Status.ACTIVE);
         listing.setAvailabilityStatus("In Stock");
         listing.setHash(HashUtil.generateShortHash());
    }



    @Named("statusToString")
    public String statusToString(Status status) {
        return EnumUtils.statusToString(status);
    }

    @Named("stringToStatus")
    public Status stringToStatus(String str) {
        return EnumUtils.stringToStatus(str);
    }


    public abstract List<ListingDTO> toDtoList(List<Listing> listings);
    public abstract List<Listing> toEntityList(List<ListingDTO> listings);
    public abstract List<ReviewDTO> reviewsToDtoList(List<Review> entities);
}


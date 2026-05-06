package com.techlab.store.mapper;

import com.techlab.store.dto.CreateListingDTO;
import com.techlab.store.dto.UpdateListingDTO;
import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.enums.Status;

import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.techlab.store.exceptions.CustomExceptions.ProductNotFoundException;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.utils.HashUtil;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ListingMapper {

    @Autowired
    ProductRepository productRepository;

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.brand", target = "brand")
    @Mapping(source = "product.sku", target = "sku")
    @Mapping(source = "product.tags", target = "tags")
    @Mapping(source = "product.rating", target = "rating")
    @Mapping(source = "product.reviews", target = "reviews")
    @Mapping(source = "createdAt", target = "meta.createdAt")
    @Mapping(source = "updatedAt", target = "meta.updatedAt")
    @Mapping(source = "deletedAt", target = "meta.deletedAt")
    @Mapping(target = "category", ignore = true)
    public abstract ListingDTO toDto(Listing listing);

    @Mapping(source = "user.email", target = "reviewerEmail")
    @Mapping(source = "user.username", target = "reviewerName")
    @Mapping(source = "product.id", target = "productId")
    public abstract ReviewDTO ReviewToDto(Review entity);

    @InheritInverseConfiguration
    @Mapping(source = "dto", target = "product")
    public abstract Listing toEntity(ListingDTO dto);

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "productName", target = "name")
    @Mapping(source = "sku", target = "sku")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "brand", target = "brand")
    @Mapping(target = "meta", ignore = true)
    @Mapping(target = "category", ignore = true)
    public abstract Product productFromDto(ListingDTO dto);

    @Mapping(target = "productId", source = "id") // <--
    @Mapping(target = "productName", source = "name")
    @Mapping(target = "category", ignore = true)
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
    @Mapping(target = "pendingReviews", ignore = true)
    public abstract Listing updateFromEntity(
        Listing update, 
        @MappingTarget Listing listing);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    public abstract Listing toEntity(CreateListingDTO dto);

    @Mapping(target = "status", ignore = true)
    public abstract Listing toEntity(UpdateListingDTO dto);

    @AfterMapping
    protected void listingAfterMapping(
        @MappingTarget Listing listing, 
        CreateListingDTO dto) 
    {
       Product existingProduct =  productRepository
                   .findBySku(dto.sku())
                   .orElseThrow(() -> new ProductNotFoundException());
        listing.setProduct(existingProduct);
        listing.getProduct().setStatus(Status.ACTIVE);
        listing.setStatus(Status.ACTIVE);
        listing.setHash(HashUtil.generateShortHash());
        listing.setAvailabilityStatus("In Stock");
    }


    public abstract List<ListingDTO> toDtoList(List<Listing> listings);
    public abstract List<Listing> toEntityList(List<ListingDTO> listings);
    public abstract List<ReviewDTO> reviewsToDtoList(List<Review> entities);
}


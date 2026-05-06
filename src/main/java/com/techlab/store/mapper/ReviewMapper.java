package com.techlab.store.mapper;

import java.time.LocalDate;
import java.time.LocalTime;

import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.entity.PendingReview;

import org.mapstruct.*;

import java.util.List;

import com.techlab.store.dto.PendingReviewDTO;
import com.techlab.store.entity.User;
import com.techlab.store.service.ListingService;
import com.techlab.store.service.ProductService;
import com.techlab.store.service.UserService;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ReviewMapper {


    // TODO resolver datos reviewer se mapean en null
    // Establecer relacion Product/Review en el afterMapping; 
    @Mapping(target = "product", ignore = true)
    public abstract Review toEntity(ReviewDTO reviewDto, @Context Product parent);

    @Mapping(target = "reviewerName", source = "user.username")
    @Mapping(target = "reviewerEmail", source = "user.email")
    @Mapping(target = "productId", source = "product.id")
    public abstract ReviewDTO toDto(Review reviewDto);

    @AfterMapping
    protected void linkReviewToParent(
            ReviewDTO reviewDto,
            @MappingTarget Review review,
            @Context Product parent) {
        review.setProduct(parent);
    }

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "listingId", source = "listing.id")
    @Mapping(target = "image", source = "listing.thumbnail")
    @Mapping(target = "title", source = "listing.title")
    public abstract PendingReviewDTO pendingToDto(PendingReview entity);


    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "listing", ignore = true)
    public abstract PendingReview pedingToEntity(PendingReviewDTO dto);

}

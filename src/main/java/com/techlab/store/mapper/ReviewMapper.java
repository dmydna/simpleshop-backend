package com.techlab.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.entity.Review;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ReviewMapper {


    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "listingId", source = "listingId")
    public abstract Review toEntity(ReviewDTO reviewDto);


    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "productId", source = "product.id")
    public abstract ReviewDTO toDto(Review reviewDto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "listingId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    public abstract Review updateFromEntity(
        Review update, 
        @MappingTarget Review review
    );

}

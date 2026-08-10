package com.techlab.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.entity.Review;
import com.techlab.store.service.HashidService;

@Mapper(componentModel = "spring", 
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {HashidService.class}
)
public abstract class ReviewMapper {


    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "listingId", source = "listingId", qualifiedByName = "decodeId")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    public abstract Review toEntity(ReviewDTO reviewDto);



    @Mapping(target = "id", source = "id", qualifiedByName = "encodeId")    
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "productId", source = "product.id", qualifiedByName = "encodeId")
    @Mapping(target = "userPic", source = "user.image")
    @Mapping(target = "meta", ignore = true)
    public abstract ReviewDTO toDto(Review review);


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

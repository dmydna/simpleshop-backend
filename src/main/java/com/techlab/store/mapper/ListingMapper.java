package com.techlab.store.mapper;

import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ListingMapper {


    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target= "productName")
    ListingDTO toDto(Listing listing);

    @InheritInverseConfiguration
    @Mapping(source = ".", target = "product")
    Listing toEntity(ListingDTO dto);

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "productName", target = "name")
    Product productFromDto(ListingDTO dto);

    @Mapping(target = "listing", ignore = true) // Lo asignamos manualmente en el AfterMapping
    Review toReviewEntity(ReviewDTO reviewDto, @Context Listing parent);

    @AfterMapping
    default void linkReviewToParent(
            ReviewDTO reviewDto,
            @MappingTarget Review review,
            @Context Listing parent) {
        review.setListing(parent);
    }

    @Mapping(target = "images", ignore = true)
    void updateFromDto(ListingDTO dto, @MappingTarget Listing listing);

    List<ListingDTO> toDtoList(List<Listing> listings);
    List<Listing> toEntityList(List<ListingDTO> listings);
}

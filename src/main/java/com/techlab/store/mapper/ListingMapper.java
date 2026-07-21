package com.techlab.store.mapper;

import java.util.List;

import org.mapstruct.Context;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.techlab.store.dto.CreateListingDTO;
import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.ListingDraftDTO;
import com.techlab.store.dto.ListingSummary;
import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.dto.UpdateListingDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;



@Mapper(
    componentModel = "spring", 
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, 
    uses = { ListingMappingHelper.class } )     
public interface ListingMapper {

    @Mapping(target = "sku", expression = "java(listing.getProduct() != null ? listing.getProduct().getSku() : null)")
    @Mapping(source = "product.tags", target = "tags")
    @Mapping(source = "product.rating", target = "rating")
    @Mapping(target = "finalPrice", expression = "java(listingMappingHelper.calculateDiscount(listing))")
    @Mapping(source = "createdAt", target = "meta.createdAt")
    @Mapping(source = "updatedAt", target = "meta.updatedAt")
    @Mapping(source = "deletedAt", target = "meta.deletedAt")
    @Mapping(source = "status", target = "meta.status", qualifiedByName = "statusToString")
    ListingDraftDTO toDraftDto(Listing listing);

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
    @Mapping(target = "finalPrice", expression = "java(listingMappingHelper.calculateDiscount(listing))")
    ListingDTO toDto(Listing listing);


    @Mapping(source = "createdAt", target = "meta.createdAt")
    @Mapping(source = "updatedAt", target = "meta.updatedAt")
    @Mapping(source = "deletedAt", target = "meta.deletedAt")
    @Mapping(source = "status",    target = "meta.status", qualifiedByName = "statusToString")
    @Mapping(target = "finalPrice", expression = "java(listingMappingHelper.calculateDiscount(listing))")
    @Mapping(source = "product.tags", target = "tags")
    ListingSummary toSummaryDto(Listing listing);

    @Mapping(source = "product.tags", target = "tags")
    @Mapping(source = "createdAt", target = "meta.createdAt")
    @Mapping(source = "updatedAt", target = "meta.updatedAt")
    @Mapping(source = "deletedAt", target = "meta.deletedAt")
    @Mapping(source = "status",    target = "meta.status", qualifiedByName = "statusToString")
    @Mapping(target = "finalPrice", expression = "java(listingMappingHelper.calculateDiscount(listing))")
    ListingSummary toSummaryFull(Listing listing);


    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.image", target = "userPic")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "comment", target = "comment")
    @Mapping(source = "rating", target = "rating")
    @Mapping(target = "meta", ignore = true)
    ReviewDTO ReviewToDto(Review entity);

    @InheritInverseConfiguration(name = "toDto") 
    @Mapping(source = "meta.status", target = "status", qualifiedByName = "stringToStatus")
    @Mapping(source = "dto", target = "product")
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    Listing toEntity(ListingDTO dto);

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "productName", target = "name")
    @Mapping(source = "sku", target = "sku")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "brand", target = "brand")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "listings", ignore = true)
    @Mapping(target = "meta", ignore = true)
    Product productFromDto(ListingDTO dto);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    Review toReviewEntity(ReviewDTO reviewDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "meta", ignore = true)
    @Mapping(target = "productId", source = "id")
    @Mapping(target = "productName", source = "name")
    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "sku", source = "sku")
    @Mapping(target = "tags", source = "tags")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "reviews", source = "reviews")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "weight", source = "weight")    
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "finalPrice", ignore = true)
    @Mapping(target = "discountPercentage", ignore = true)
    @Mapping(target = "warrantyInformation", ignore = true)
    @Mapping(target = "shippingInformation", ignore = true)
    @Mapping(target = "availabilityStatus", ignore = true)
    @Mapping(target = "returnPolicy", ignore = true)
    @Mapping(target = "minimumOrderQuantity", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "thumbnail", ignore = true)
    @Mapping(target = "hash", ignore = true)
    @Mapping(target = "stock", ignore = true)    
    ListingDTO productToDto(Product product);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true) // Lo asignamos manualmente en el AfterMapping
    Review toReviewEntity(
        ReviewDTO reviewDto, 
        @Context Product parent
    );


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Listing updateFromDto(
        ListingDTO dto,
        @MappingTarget Listing listing
    );


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    Listing updateFromEntity(
        Listing update, 
        @MappingTarget Listing listing);

    @InheritInverseConfiguration(name = "toDto") 
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "availabilityStatus", ignore = true)
    @Mapping(target = "hash", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    Listing toEntity(CreateListingDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "availabilityStatus", ignore = true)
    @Mapping(target = "hash", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    Listing toEntity(UpdateListingDTO dto);


    List<ListingDTO> toDtoList(List<Listing> listings);
    List<Listing> toEntityList(List<ListingDTO> listings);
    List<ReviewDTO> reviewsToDtoList(List<Review> entities);
}


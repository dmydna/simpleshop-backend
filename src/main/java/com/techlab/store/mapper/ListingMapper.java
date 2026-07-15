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
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.*;
import java.util.List;



@Mapper(
    componentModel = "spring", 
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, 
    uses = { ListingMappingHelper.class } )     
public interface ListingMapper {

    @Mapping(target = "sku", expression = "java(listing.getProduct() != null ? listing.getProduct().getSku() : null)")
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
    ReviewDTO ReviewToDto(Review entity);

    @InheritInverseConfiguration(name = "toDto") 
    @Mapping(source = "meta.status", target = "status", qualifiedByName = "stringToStatus")
    @Mapping(source = "dto", target = "product")
    Listing toEntity(ListingDTO dto);

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "productName", target = "name")
    @Mapping(source = "sku", target = "sku")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "brand", target = "brand")
    @Mapping(target = "meta", ignore = true)
    Product productFromDto(ListingDTO dto);

    @Mapping(target = "productId", source = "id") // <--
    @Mapping(target = "productName", source = "name")
    ListingDTO productToDto(Product product);

    @Mapping(target = "product", ignore = true) // Lo asignamos manualmente en el AfterMapping
    Review toReviewEntity(
        ReviewDTO reviewDto, 
        @Context Product parent
    );


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true)
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
    Listing toEntity(CreateListingDTO dto);

    Listing toEntity(UpdateListingDTO dto);


    List<ListingDTO> toDtoList(List<Listing> listings);
    List<Listing> toEntityList(List<ListingDTO> listings);
    List<ReviewDTO> reviewsToDtoList(List<Review> entities);
}


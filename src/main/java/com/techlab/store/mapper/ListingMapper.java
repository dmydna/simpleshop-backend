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
    @Mapping(source = "product.brand", target= "brand")
    @Mapping(source = "product.sku", target= "sku")
    @Mapping(source = "product.stock", target= "stock")
    @Mapping(source = "product.category", target= "category")
    @Mapping(source = "product.tags", target= "tags")
    @Mapping(source = "product.rating", target= "rating")
    @Mapping(source = "product.reviews", target= "reviews")
    ListingDTO toDto(Listing listing);



    @Mapping(target = "productId", source = "product.id")
    ReviewDTO ReviewToDto(Review entity); 


    @InheritInverseConfiguration
    @Mapping(source = "dto", target = "product")
    Listing toEntity(ListingDTO dto);

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "productName", target = "name")
    Product productFromDto(ListingDTO dto);


    @Mapping(target = "productId", source = "id") // <--
    @Mapping(target = "productName", source = "name")
    ListingDTO productToDto(Product product);

    @Mapping(target = "product", ignore = true) // Lo asignamos manualmente en el AfterMapping
    Review toReviewEntity(ReviewDTO reviewDto, @Context Product parent);

    @AfterMapping
    default void linkReviewToParent(
            ReviewDTO reviewDto,
            @MappingTarget Review review,
            @Context Product parent) {
        review.setProduct(parent);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true)
    void updateFromDto(ListingDTO dto, @MappingTarget Listing listing);

    List<ListingDTO> toDtoList(List<Listing> listings);
    List<Listing> toEntityList(List<ListingDTO> listings);
    List<ReviewDTO> reviewsToDtoList(List<Review> entities); 
}

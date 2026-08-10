package com.techlab.store.mapper;

import java.time.LocalDateTime;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.techlab.store.dto.CreateProductDTO;
import com.techlab.store.dto.ProductDTO;
import com.techlab.store.dto.UpdateProductDTO;
import com.techlab.store.entity.Product;
import com.techlab.store.enums.ListingStatus;
import com.techlab.store.enums.Status;
import com.techlab.store.service.HashidService;
import com.techlab.store.utils.EnumUtils;





@Mapper(componentModel = "spring", 
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, 
    uses = {HashidService.class})
public interface ProductMapper {

    // DTO → Entity
    @Mapping(source = "dimensions.width",  target = "dimensions.width") 
    @Mapping(source = "dimensions.height", target = "dimensions.height") 
    @Mapping(source = "dimensions.depth",  target = "dimensions.depth") 
    @Mapping(target = "meta", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "listings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(source = "category", target = "category")
    Product toEntity(ProductDTO dto);

    @Mapping(target = "status", source = "status")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "meta", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "listings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Product toEntity(CreateProductDTO dto);

    @Mapping(source = "dimensions.width",  target = "dimensions.width") 
    @Mapping(source = "dimensions.height", target = "dimensions.height") 
    @Mapping(source = "dimensions.depth",  target = "dimensions.depth") 
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "meta", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "listings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Product toEntity(UpdateProductDTO dto);


    // Entity → DTO
    @Mapping(source = "id", target = "id", qualifiedByName = "encodeId")
    @Mapping(source = "dimensions.width",  target = "dimensions.width") 
    @Mapping(source = "dimensions.height", target = "dimensions.height") 
    @Mapping(source = "dimensions.depth",  target = "dimensions.depth") 
    @Mapping(source = "createdAt", target = "meta.createdAt")    
    @Mapping(source = "deletedAt", target = "meta.deletedAt")    
    @Mapping(source = "updatedAt", target = "meta.updatedAt")    
    @Mapping(source = "status",    target = "meta.status", qualifiedByName = "statusToString")
    @Mapping(source = "category",  target = "category")
    ProductDTO toDto(Product product);

    // ignoramos relaciones y campos sensibles(status)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reviews", ignore = true) 
    public Product updateFromEntity(
        Product dataToEdit, 
        @MappingTarget Product product
    );



    // after creating
    @AfterMapping
    default void productAfterMapping(
        @MappingTarget Product product, 
        CreateProductDTO dto
    ) {
        product.setCreatedAt(LocalDateTime.now());
        product.setStatus(Status.ACTIVE);
    }


    @Named("statusToString")
    default String statusToString(Status status) {
        return EnumUtils.statusToString(status);
    }

    @Named("stringToStatus")
    default ListingStatus stringToStatus(String str) {
        return EnumUtils.stringToListingStatus(str);
    }


}

package com.techlab.store.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.*;
import com.techlab.store.dto.ProductDTO;
import com.techlab.store.dto.UpdateProductDTO;
import com.techlab.store.dto.CreateProductDTO;
import com.techlab.store.enums.Status;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Category;
import com.techlab.store.service.CategoryService;
import java.time.LocalDateTime;



@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    // DTO → Entity
    @Mapping(target = "meta", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(source = "category", target = "category", ignore = true)
    Product toEntity(ProductDTO dto);

    @Mapping(target = "meta", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(CreateProductDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "meta", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(UpdateProductDTO dto);


    // Entity → DTO
    @Mapping(source = "dimensions.width", target = "dimensions.width") 
    @Mapping(source = "dimensions.height", target = "dimensions.height") 
    @Mapping(source = "dimensions.depth", target = "dimensions.depth") 
    @Mapping(source = "createdAt", target = "meta.createdAt")    
    @Mapping(source = "deletedAt", target = "meta.deletedAt")    
    @Mapping(source = "updatedAt", target = "meta.updatedAt")    
    @Mapping(source = "category", target = "category")
    ProductDTO toDto(Product product);

    // ignoramos relaciones y campos sensibles(status)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "pendingReviews", ignore = true)
    @Mapping(target = "reviews", ignore = true) 
    public Product updateFromEntity(Product dataToEdit, @MappingTarget Product product);

    // after creating
    @AfterMapping
    default void productAfterMapping(@MappingTarget Product product, CreateProductDTO dto) {
        product.setCreatedAt(LocalDateTime.now());
        product.setStatus(Status.ACTIVE);
    }

}

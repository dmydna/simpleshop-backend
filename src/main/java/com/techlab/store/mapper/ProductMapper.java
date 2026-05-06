package com.techlab.store.mapper;

import org.mapstruct.AfterMapping;

import com.techlab.store.dto.ProductDTO;
import com.techlab.store.dto.UpdateProductDTO;
import com.techlab.store.dto.CreateProductDTO;
import com.techlab.store.enums.Status;

import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;

import org.mapstruct.*;

import com.techlab.store.entity.Category;
import com.techlab.store.service.CategoryService;

import java.time.LocalDate;

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

    @AfterMapping
    default void mapCategoryToEntity(ProductDTO dto, @MappingTarget Product product, 
                                     CategoryService categoryService) {
        if (dto.category() != null && !dto.category().isEmpty()) {
            Category category = categoryService
               .findOrCreateCategoryFromPath(dto.category());
            product.setCategory(category);
        }
    }

    // Entity → DTO
    @Mapping(source = "dimensions.width", target = "dimensions.width") 
    @Mapping(source = "dimensions.height", target = "dimensions.height") 
    @Mapping(source = "dimensions.depth", target = "dimensions.depth") 
    @Mapping(source = "createdAt", target = "meta.createdAt")    
    @Mapping(source = "deletedAt", target = "meta.deletedAt")    
    @Mapping(source = "updatedAt", target = "meta.updatedAt")    
    @Mapping(source = "category", target = "category", qualifiedByName = "categoryToPath")
    ProductDTO toDto(Product product);

    @Named("categoryToPath")
    default String categoryToPath(Category category) {
        if (category == null) {
            return null;
        }
        return buildCategoryPath(category);
    }

    default String buildCategoryPath(Category category) {
        if (category.getParentCategory() == null) {
            return category.getName();
        }
        return buildCategoryPath(category.getParentCategory()) + "/" + category.getName();
    }



    // ignoramos relaciones y campos sensibles(status)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "pendingReviews", ignore = true)
    @Mapping(target = "reviews", ignore = true) 
    public Product updateFromEntity(Product dataToEdit, @MappingTarget Product product);


    // after creating
    @AfterMapping
    default void productAfterMapping(@MappingTarget Product product, CreateProductDTO dto) {
        product.setCreatedAt(LocalDate.now());
        product.setStatus(Status.ACTIVE);
    }



}

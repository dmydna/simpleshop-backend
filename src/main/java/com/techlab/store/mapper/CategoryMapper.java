package com.techlab.store.mapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.techlab.store.dto.ProductDTO;
import com.techlab.store.entity.Category;
import com.techlab.store.entity.Product;
import com.techlab.store.service.CategoryService;



@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
    
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

    @AfterMapping
    default void mapCategoryToEntity(
        ProductDTO dto, 
        @MappingTarget Product product, 
        CategoryService categoryService
    ) {
        if (dto.category() != null && !dto.category().isEmpty()) {
            Category category = categoryService
               .findOrCreateCategoryFromPath(dto.category());
        }
    }

}
package com.techlab.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techlab.store.entity.Category;
import com.techlab.store.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category findOrCreateCategoryFromPath(String categoryPath) {
        if (categoryPath == null || categoryPath.trim().isEmpty()) {
            throw new IllegalArgumentException("El path de categoría no puede estar vacío");
        }

        String[] parts = categoryPath.split("/");
        Category currentCategory = null;

        for (String part : parts) {
            part = part.trim();
            
            if (part.isEmpty()) {
                throw new IllegalArgumentException("El path contiene partes vacías");
            }

            Category category = categoryRepository.findByNameAndParentCategory(part, currentCategory);

            if (category == null) {
                category = new Category();
                category.setName(part);
                category.setParentCategory(currentCategory);
                category = categoryRepository.save(category);
            }

            currentCategory = category;
        }

        return currentCategory;
    }


    public Long saveCategory(String categoryPath) {
        String[] parts = categoryPath.split("/");
        Category parent = null;

        for (String part : parts) {
            var category = categoryRepository
              .findByNameAndParentCategory(part, parent);

            if (category == null) {
                category = new Category();
                category.setName(part);
                category.setParentCategory(parent);
                category = categoryRepository.save(category);
            }
            parent = category;
        }

        return parent.getId();
    }


}
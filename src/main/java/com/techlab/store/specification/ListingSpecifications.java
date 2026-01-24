package com.techlab.store.specification;


import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.techlab.store.entity.Listing;

import jakarta.persistence.criteria.Predicate;

public class ListingSpecifications {

    public static Specification<Listing> hasCategories(List<String> categories) {
        return (root, query, cb) -> {
            if (categories == null || categories.isEmpty()) return null;
            // Unimos Listing con Product y filtramos por categoría
            return root.join("product").get("category").in(categories);
        };
    }

    public static Specification<Listing> hasTags(List<String> tags) {
        return (root, query, cb) -> {
            if (tags == null || tags.isEmpty()) return null;
            // IMPORTANTE: Para colecciones como tags, se requiere un Join
            return root.join("product").join("tags").in(tags);
        };
    }


    public static Specification<Listing> hasTitle(String title) {
        return (root, query, cb) -> {
            // 1. Si el título es nulo o está vacío, no filtramos nada
            if (title == null || title.trim().isEmpty()) {
                return null;
            }
    
            // 2. Creamos el patrón de búsqueda: %texto%
            String pattern = "%" + title.toLowerCase() + "%";
    
            // 3. Generamos el predicado: lower(title) LIKE %texto%
            return cb.like(cb.lower(root.get("title")), pattern);
        };
    }


    public static Specification<Listing> priceInRange(Double min, Double max) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (predicates.isEmpty()) {
                return null; // No aplica ningún filtro de precio
            }
            
            if (min != null) {
                predicates.add(cb.ge(root.get("price"), min));
            }
            if (max != null) {
                predicates.add(cb.le(root.get("price"), max));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
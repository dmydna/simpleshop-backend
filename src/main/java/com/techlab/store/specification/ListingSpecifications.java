package com.techlab.store.specification;


import java.util.ArrayList;
import java.util.List;

import com.techlab.store.entity.User;

import com.techlab.store.enums.Visibility;
import com.techlab.store.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import com.techlab.store.entity.Listing;

import jakarta.persistence.criteria.Predicate;
import org.springframework.util.CollectionUtils;




@RequiredArgsConstructor
public class ListingSpecifications {

    private final StringUtils stringUtils;

    public static Specification<Listing> isNotDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedDate"));
    }

    public static Specification<Listing> hasVisibilty(Visibility visibility) {
        return (root, query, cb) -> {
            if (visibility == null) return null;
            // Unimos Listing con Product y filtramos por categoría
            return cb.equal(root.get("visibility"), visibility);
        };
    }

    public static Specification<Listing> hasCategories(List<String> categories) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(categories)) return null;
            // Unimos Listing con Product y filtramos por categoría
            return root.join("product").get("category").in(categories);
        };
    }


    public static Specification<Listing> hasTags(List<String> tags) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(tags)) return null;
            // IMPORTANTE: Para colecciones como tags, se requiere un Join
            return root.join("product").join("tags").in(tags);
        };
    }


    public static Specification<Listing> hasTitle(String title) {
        return (root, query, cb) -> {
            // 1. Si el título es nulo o está vacío, no filtramos nada
            if (!StringUtils.hasText(title)) {
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

    public static Specification<Listing> hasId(Long id) {
        return (root, query, cb) -> {
            if (id == null) return null;
            return cb.equal(root.get("id"), id);
        };
    }
}
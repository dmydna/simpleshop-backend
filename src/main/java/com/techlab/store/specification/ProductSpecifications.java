package com.techlab.store.specification;


import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import com.techlab.store.entity.Product;
import com.techlab.store.enums.Status;
import com.techlab.store.utils.StringUtils;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;




@RequiredArgsConstructor
public class ProductSpecifications {

    private final StringUtils stringUtils;
    
    public static Specification<Product> isNotDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(category)) return null;
            // Unimos Product con Product y filtramos por categoría
            return cb.equal(root.get("category"), category);
        };
    }


    public static Specification<Product> hasStatus(Status status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            // Unimos Product con Product y filtramos por categoría
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Product> hasTags(List<String> tags) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(tags)) return null;
            // IMPORTANTE: Para colecciones como tags, se requiere un Join
            return root.join("tags").in(tags);
        };
    }


    public static Specification<Product> hasSku(String sku) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(sku)) return null;
            // IMPORTANTE: Para colecciones como tags, se requiere un Join
            return cb.equal(root.get("sku"), sku);
        };
    }


    public static Specification<Product> hasName(String name) {
        return (root, query, cb) -> {
            // 1. Si el título es nulo o está vacío, no filtramos nada
            if (!StringUtils.hasText(name)) {
                return null;
            }
    
            // 2. Creamos el patrón de búsqueda: %texto%
            String pattern = "%" + name.toLowerCase() + "%";
    
            // 3. Generamos el predicado: lower(name) LIKE %texto%
            return cb.like(cb.lower(root.get("name")), pattern);
        };
    }


    // REVISION PENDIENTE
    public static Specification<Product> priceInRange(Double min, Double max) {
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

    public static Specification<Product> hasId(Long id) {
        return (root, query, cb) -> {
            if (id == null) return null;
            return cb.equal(root.get("id"), id);
        };
    }
}
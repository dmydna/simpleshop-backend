package com.techlab.store.specification;



import org.springframework.data.jpa.domain.Specification;

import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Review;
import com.techlab.store.utils.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import com.techlab.store.enums.ReviewStatus;



@RequiredArgsConstructor
public class ReviewSpecifications {
    private final StringUtils stringUtils;


    public static Specification<Review> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return cb.conjunction();
            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    public static Specification<Review> hasProductId(Long productId) {
        return (root, query, cb) -> {
            if (productId == null) return cb.conjunction();
            return cb.equal(root.get("product").get("id"), productId);
        };
    }


    public static Specification<Review> hasActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) return cb.conjunction();
            return cb.equal(root.get("reviewed"), active);
        };
    }



    public static Specification<Review> hasStatus(ReviewStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            // Unimos Listing con Product y filtramos por categoría
            return cb.equal(root.get("status"), status);
        };
    }


    public static Specification<Review> hasId(Long id) {
        return (root, query, cb) -> {
            if (id == null) return cb.conjunction();
            return cb.equal(root.get("id"), id);
        };
    }

}

package com.techlab.store.specification;

import org.springframework.data.jpa.domain.Specification;
import com.techlab.store.entity.PendingReview;
import com.techlab.store.utils.StringUtils;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class PendingReviewSpecifications {
    private final StringUtils stringUtils;


    public static Specification<PendingReview> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return cb.conjunction();
            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    public static Specification<PendingReview> hasProductId(Long productId) {
        return (root, query, cb) -> {
            if (productId == null) return cb.conjunction();
            return cb.equal(root.get("product").get("id"), productId);
        };
    }


    public static Specification<PendingReview> hasActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) return cb.conjunction();
            return cb.equal(root.get("reviewed"), active);
        };
    }



    public static Specification<PendingReview> hasId(Long id) {
        return (root, query, cb) -> {
            if (id == null) return cb.conjunction();
            return cb.equal(root.get("id"), id);
        };
    }

}

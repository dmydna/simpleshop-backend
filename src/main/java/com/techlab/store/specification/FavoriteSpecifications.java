package com.techlab.store.specification;


import org.springframework.data.jpa.domain.Specification;

import com.techlab.store.entity.Favorite;
import com.techlab.store.utils.StringUtils;

import lombok.RequiredArgsConstructor;




@RequiredArgsConstructor
public class FavoriteSpecifications {
    private final StringUtils stringUtils;

    public static Specification<Favorite> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return cb.conjunction();
            return cb.equal(root.get("user").get("id"), userId);
        };
    }


    public static Specification<Favorite> hasId(Long id) {
        return (root, query, cb) -> {
            if (id == null) return cb.conjunction();
            return cb.equal(root.get("id"), id);
        };
    }

}
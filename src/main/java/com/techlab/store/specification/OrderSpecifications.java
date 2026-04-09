package com.techlab.store.specification;


import org.springframework.data.jpa.domain.Specification;

import com.techlab.store.entity.Order;
import com.techlab.store.utils.StringUtils;

import lombok.RequiredArgsConstructor;




@RequiredArgsConstructor
public class OrderSpecifications {
    private final StringUtils stringUtils;

    public static Specification<Order> hasClientId(Long clientId) {
        return (root, query, cb) -> {
            if (clientId == null) return cb.conjunction();
            return cb.equal(root.get("client").get("id"), clientId);
        };
    }


    public static Specification<Order> hasStatus(Order.OrderState status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("state"), status);
        };
    }
}
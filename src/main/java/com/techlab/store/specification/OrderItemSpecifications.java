package com.techlab.store.specification;


import org.springframework.data.jpa.domain.Specification;

import com.techlab.store.entity.OrderItem;
import com.techlab.store.enums.OrderStatus;
import com.techlab.store.utils.StringUtils;

import lombok.RequiredArgsConstructor;




@RequiredArgsConstructor
public class OrderItemSpecifications {
    private final StringUtils stringUtils;

    public static Specification<OrderItem> hasClientId(Long clientId) {
        return (root, query, cb) -> {
            if (clientId == null) return cb.conjunction();
            return cb.equal(
                root.get("order")
                    .get("client")
                    .get("id"), clientId);
        };
    }


    public static Specification<OrderItem> hasStatus(OrderStatus status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }
}
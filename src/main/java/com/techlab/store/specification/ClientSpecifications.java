package com.techlab.store.specification;


import org.springframework.data.jpa.domain.Specification;

import com.techlab.store.utils.StringUtils;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.Favorite;

import lombok.RequiredArgsConstructor;




@RequiredArgsConstructor
public class ClientSpecifications {


    public static Specification<Client> isNotDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Client> hasFirstName(String firstname) {
        return (root, query, cb) -> {
            if (firstname == null || firstname.trim().isEmpty()) return cb.conjunction();

            // 3. Generamos el predicado: lower(title) LIKE %texto%
            return cb.like(cb.lower(root.get("firtsname")), StringUtils.toLikePattern(firstname));
        };
    }

    public static Specification<Client> hasLastName(String lastname) {
        return (root, query, cb) -> {
            if (lastname == null || lastname.trim().isEmpty()) return cb.conjunction();
            return cb.like(cb.lower(root.get("lastName")), StringUtils.toLikePattern(lastname));
        };
    }


}
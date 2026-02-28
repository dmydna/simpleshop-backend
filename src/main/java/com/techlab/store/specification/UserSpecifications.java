package com.techlab.store.specification;

import com.techlab.store.entity.Listing;
import com.techlab.store.entity.User;
import com.techlab.store.utils.StringUtils;
import jakarta.persistence.criteria.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;


public class UserSpecifications {


    public static Specification<User> isNotDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedDate"));
    }
    public static Specification<User> hasClientName(String clientname) {
        return (root, query, builder) -> {
            // 1. Si el título es nulo o está vacío, no filtramos nada
            if (!StringUtils.hasText(clientname)) {return null;}
            // 2. Creamos el patrón de búsqueda: %texto%
            String pattern = "%" + clientname.toLowerCase() + "%";
            Path<String> clientName = root.join("client").get("firstName");  // User.Client.name
            return builder.like(builder.lower(clientName), pattern);
        };
    }
    public static Specification<User> hasUsername(String username) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(username)) {return null;}
            String pattern = "%" + username.toLowerCase() + "%";
            Path<String> usernamePath = root.get("username"); // User.username
            return builder.like(builder.lower(usernamePath), pattern);
        };
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(email)) {return null;}
            String pattern = "%" + email.toLowerCase() + "%";
            Path<String> emailPath = root.get("email"); // User.username
            return builder.like(builder.lower(emailPath), pattern);
        };
    }


}

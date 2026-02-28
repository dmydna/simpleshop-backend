package com.techlab.store.repository;

import com.techlab.store.entity.Product;
import com.techlab.store.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<User>  findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<User> findAll(Pageable pageable);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String name);
    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"client"}) // opcional
    Page<User> findAll(Specification<User> spec, Pageable pageable);
}
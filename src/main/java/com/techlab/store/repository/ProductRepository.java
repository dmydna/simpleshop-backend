package com.techlab.store.repository;

import com.techlab.store.entity.Product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  List<Product> findByNameContainingIgnoreCase(String name);

  List<Product> findByCategoryContainingIgnoreCase(String category);

  List<Product> findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(String name, String category);

  @Query("SELECT l FROM Product l WHERE l.sku = :sku")
  Optional<Product> findBySku(@Param("sku") String sku);

}

package com.techlab.store.repository;

import com.techlab.store.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  List<Product> findByTitleContainingIgnoreCase(String name);

  List<Product> findByCategoryContainingIgnoreCase(String category);

  List<Product> findByTitleContainingIgnoreCaseAndCategoryContainingIgnoreCase(String name, String category);
}

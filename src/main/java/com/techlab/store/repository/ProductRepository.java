package com.techlab.store.repository;

import com.techlab.store.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

  Page<Product>  findByNameContainingIgnoreCase(String name, Pageable pageable);
  Page<Product>  findByCategoryContainingIgnoreCase(String category, Pageable pageable);
  Page<Product>  findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(String name, String category, Pageable pageable);

  List<Product> findBySkuIn(Set<String> skus);

  @Query("SELECT p FROM Product p WHERE p.sku = :sku")
  Optional<Product> findBySku(@Param("sku") String sku);

  @Query("SELECT p FROM Product p WHERE p.sku = :sku AND p.deletedAt IS NULL")
  Optional<Product> findActiveBySku(@Param("sku") String sku);

}

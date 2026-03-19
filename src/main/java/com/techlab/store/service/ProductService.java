package com.techlab.store.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.techlab.store.entity.Product;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.utils.StringUtils;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final StringUtils stringUtils;

  public Product createProduct(Product product){
    log.info("Producto ingresado: {}", product);

    // productSavedWithId
    return this.productRepository.save(product);
  }

  public Product getById(Long id){
    Optional<Product> productOptional = this.productRepository.findById(id);

    if (productOptional.isEmpty()){
      throw new RuntimeException("Producto no encontrado con ID: " + id);
    }

    return productOptional.get();
  }

  public Page<Product> getAll(String name, String category, Pageable pageable) {

    boolean hasName = name != null && !name.isEmpty();
    boolean hasCategory = category != null && !category.isEmpty();

    if (hasName && hasCategory) {
        return this.productRepository
          .findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(name, category, pageable);
    }

    if (hasName) {
        return this.productRepository
         .findByNameContainingIgnoreCase(name, pageable);
    }

    if (hasCategory) {
        return this.productRepository
         .findByCategoryContainingIgnoreCase(category, pageable);
    }

    return this.productRepository
      .findAll(pageable);
  }

  public Product updateById(Long id, Product dataToEdit){
    Product product = this.getById(id);

    if (!stringUtils.isEmpty(dataToEdit.getName())){
        System.out.printf("Editando el nombre del producto: viejo:%s - nuevo:%s", product.getName(), dataToEdit.getName());
      product.setName(dataToEdit.getName());
    }
    if (!stringUtils.isEmpty(dataToEdit.getBrand())) product.setBrand(dataToEdit.getBrand());
    if (!stringUtils.isEmpty(dataToEdit.getCategory())) product.setCategory(dataToEdit.getCategory());
    product.setStock(dataToEdit.getStock());
    return this.productRepository.save(product);
  }

  public Product deleteById(Long id){
    Product product = this.getById(id);

    //this.productRepository.delete(product);
    product.setDeleted(true);
    product.setDeletedDate(LocalDate.now());
    this.productRepository.save(product);

    return product;
  }

  @Transactional
  public List<Product> saveAll(List<Product> products) {
//    for (Product product : products) {
//      if (product.getReviews() != null) {
//        product.getReviews().forEach(review -> review.setProduct(product));
//      }
//    }
    return productRepository.saveAll(products);
  }
}

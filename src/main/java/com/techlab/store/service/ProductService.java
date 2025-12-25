package com.techlab.store.service;

import com.techlab.store.entity.Product;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.utils.StringUtils;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class ProductService {


  private final ProductRepository productRepository;
  private final StringUtils stringUtils;


  public ProductService(ProductRepository productRepository, StringUtils stringUtils) {
    this.productRepository = productRepository;
    this.stringUtils = stringUtils;
  }

  public Product createProduct(Product product){
    log.info("Producto ingresado: {}", product);

    // productSavedWithId
    return this.productRepository.save(product);
  }

  public Product getProductById(Long id){
    Optional<Product> productOptional = this.productRepository.findById(id);

    if (productOptional.isEmpty()){
      throw new RuntimeException("Producto no encontrado con ID: " + id);
    }

    return productOptional.get();
  }

  public List<Product> findAllProducts(String name, String category){
    if (!name.isEmpty() && !category.isEmpty()){
      return this.productRepository.findByTitleContainingIgnoreCaseAndCategoryContainingIgnoreCase(name, category);
    }

    if (!name.isEmpty()){
      return this.productRepository.findByTitleContainingIgnoreCase(name);
    }

    if (!category.isEmpty()){
      return this.productRepository.findByCategoryContainingIgnoreCase(category);
    }

    return this.productRepository.findAll();
  }

  public Product editProductById(Long id, Product dataToEdit){
    Product product = this.getProductById(id);

    if (!stringUtils.isEmpty(dataToEdit.getTitle())){
        System.out.printf("Editando el nombre del producto: viejo:%s - nuevo:%s", product.getTitle(), dataToEdit.getTitle());
      product.setTitle(dataToEdit.getTitle());
    }
    if (!stringUtils.isEmpty(dataToEdit.getBrand())) product.setBrand(dataToEdit.getBrand());
    if (!stringUtils.isEmpty(dataToEdit.getDescription())) product.setDescription(dataToEdit.getDescription());
    if (!stringUtils.isEmpty(dataToEdit.getCategory())) product.setCategory(dataToEdit.getCategory());
    product.setStock(dataToEdit.getStock());
    return this.productRepository.save(product);
  }

  public Product deleteProductById(Long id){
    Product product = this.getProductById(id);

    //this.productRepository.delete(product);
    product.setDeleted(true);
    this.productRepository.save(product);

    return product;
  }

  @Transactional
  public List<Product> saveAll(List<Product> products) {
    for (Product product : products) {
      if (product.getReviews() != null) {
        product.getReviews().forEach(review -> review.setProduct(product));
      }
    }
    return productRepository.saveAll(products);
  }
}

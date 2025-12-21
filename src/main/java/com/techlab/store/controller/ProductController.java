package com.techlab.store.controller;
import com.techlab.store.entity.Product;
import com.techlab.store.service.ProductService;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  public Product createProduct(@RequestBody Product product){
    return this.productService.createProduct(product);
  }

  @PostMapping("/bulk")
  public ResponseEntity<List<Product>> createProducts(@RequestBody List<Product> products) {
    // El service debe usar saveAll()
    List<Product> savedProducts = productService.saveAll(products);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedProducts);
  }

  @GetMapping("/{id}")
  public Product getProductById(@PathVariable Long id){
    return this.productService.getProductById(id);
  }

  @GetMapping
  public List<Product> getAllProducts(
      @RequestParam(required = false, defaultValue = "") String name,
      @RequestParam(required = false, defaultValue = "") String category){
    return this.productService.findAllProducts(name, category);
  }

  @PutMapping("/{id}")
  public Product editProductById(@PathVariable Long id, @RequestBody Product dataToEdit){
    return this.productService.editProductById(id, dataToEdit);
  }

  @DeleteMapping("/{id}")
  public Product deleteProductById(@PathVariable Long id){
    return this.productService.deleteProductById(id);
  }

}

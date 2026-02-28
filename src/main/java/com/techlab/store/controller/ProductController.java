package com.techlab.store.controller;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.store.entity.Product;
import com.techlab.store.service.ProductService;




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
    products.forEach(product -> product.setId(null));
    List<Product> savedProducts = productService.saveAll(products);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedProducts);
  }

  @GetMapping("/{id}")
  public Product getById(@PathVariable Long id){
    return this.productService.getById(id);
  }


  @GetMapping
  public ResponseEntity<Page<Product>> getAll(
      @RequestParam(required = false, defaultValue = "") String name,
      @RequestParam(required = false, defaultValue = "") String category,
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
    return ResponseEntity.ok(productService.getAll(name, category, pageable));
  }


  @PutMapping("/{id}")
  public Product updateById(@PathVariable Long id, @RequestBody Product dataToEdit){
    return this.productService.updateById(id, dataToEdit);
  }

  @DeleteMapping("/{id}")
  public Product deleteById(@PathVariable Long id){
    return this.productService.deleteById(id);
  }

}

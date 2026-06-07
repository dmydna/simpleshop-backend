package com.techlab.store.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import com.techlab.store.service.AuthService;
import com.techlab.store.service.ProductService;
import com.techlab.store.exceptions.CustomExceptions.*;
import com.techlab.store.mapper.ProductMapper;
import com.techlab.store.dto.CreateProductDTO;
import com.techlab.store.dto.ProductDTO;
import com.techlab.store.dto.UpdateProductDTO;
import com.techlab.store.entity.Product;
import com.techlab.store.enums.Status;

import lombok.RequiredArgsConstructor;




@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final AuthService authService;

    // CREATE
    @PostMapping
    public ResponseEntity<ProductDTO> create(
      @RequestBody CreateProductDTO dto){
        Product entity = productMapper.toEntity(dto);
        Product saveProduct = productService.createProduct(entity);
        ProductDTO response = productMapper.toDto(saveProduct);
        return ResponseEntity.ok(response);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        ProductDTO response = productMapper.toDto(this.productService.getById(id));
        return ResponseEntity.ok(response);
    }


    // CHECKME: GET BY SKU
    // GET BY SKU
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductDTO> getBySku(@PathVariable String sku) {
        ProductDTO response = productMapper.toDto(this.productService.getBySku(sku));
        return ResponseEntity.ok(response);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findByFilter(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String sku,
            @RequestParam(required = false, defaultValue = "") List<String> tags,
            @RequestParam(required = false, defaultValue = "") String category,
            @RequestParam(required = false, defaultValue = "") Status status,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        boolean isAdmin = authService.isAdmin(authHeader); 
        Status filterStatus = isAdmin ? status : Status.ACTIVE;
        Page<Product> filtered = productService.filter(name, sku, tags, category, filterStatus, pageable);

        return ResponseEntity.ok(filtered.map(p -> this.productMapper.toDto(p)));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateById(@PathVariable Long id, @RequestBody UpdateProductDTO dataToEdit) {
        Product entity = productMapper.toEntity(dataToEdit);
        Product savedProduct = this.productService.updateById(id, entity);
        ProductDTO response = productMapper.toDto(savedProduct);
        return ResponseEntity.ok(response);
    }


    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    // UPDATE STATUS
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductDTO> updateStatus( 
        @PathVariable Long id, 
        @RequestParam Status status) {
        Product product = productService.updateStatusById(id, status);
        ProductDTO response = productMapper.toDto(product);
        return ResponseEntity.ok(response);
    }


}

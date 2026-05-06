package com.techlab.store.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techlab.store.dto.ProductDTO;
import com.techlab.store.entity.Product;
import com.techlab.store.enums.Status;
import com.techlab.store.exceptions.CustomExceptions.*;
import com.techlab.store.exceptions.CustomExceptions.ProductHasDeletedException;
import com.techlab.store.exceptions.CustomExceptions.ProductNotFoundException;
import com.techlab.store.mapper.ProductMapper;
import com.techlab.store.repository.CategoryRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.specification.ProductSpecifications;
import com.techlab.store.utils.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StringUtils stringUtils;
    private final ProductMapper productMapper;

    public Product createProduct(Product product) {
        log.info("✅ Se CREO el producto con ID: {}", product.getId());
        return productRepository.save(product);
    }

    public Product getById(Long id) {
        Product product = this.productRepository.findById(id)
             .orElseThrow(() -> new ProductNotFoundException(id));
        log.info("✅ Se OBTUVO listing con ID {}...", id);
        return product;
    }

    public Page<Product> filter(
            String name,
            String sku,
            List<String> tags,
            String category,
            Status status,
            Pageable pageable) {

        Specification<Product> spec = Specification
            .where(ProductSpecifications.isNotDeleted())
            .and(ProductSpecifications.hasStatus(status))
            .and(ProductSpecifications.hasName(name))
            .and(ProductSpecifications.hasSku(sku))
            .and(ProductSpecifications.hasTags(tags))
            .and(ProductSpecifications.hasCategory(category));

        return productRepository.findAll(spec, pageable);
    }

    public Page<ProductDTO> findByFilter(
            String name,
            String sku,
            List<String> tags,
            String category,
            Status status,
            Pageable pageable) {

        return filter(name, sku, tags, category, status, pageable)
            .map(product -> this.productMapper.toDto(product));
    }


    @Transactional
    public Product updateStatusById(Long id, Status status){
        log.info("🔔 Actualizando STATUS de listing con ID {}...", id);
        Product product = getById(id);

        if(isDeleted(id)){ throw new ProductHasDeletedException(id);}

        if(status.equals(Status.DELETED)){ deleteById(id); }

        product.setStatus(status);
        return product;
    }

    @Transactional
    public Product updateById(Long id, Product dataToEdit) {
        log.info("🔔 Actualizando listing con ID {}...", id);
        Product product = getById(id);

        if (dataToEdit.getId() != null && !dataToEdit.getId().equals(id)) {
            throw new IllegalArgumentException("El ID del recurso no coincide con el ID en el cuerpo de la solicitud");
        }

        if(dataToEdit.getStatus() != null){ updateStatusById(id, dataToEdit.getStatus());}
        return  productMapper.updateFromEntity(dataToEdit, product);
    }

    public boolean isDeleted(Long id) {
        Product entity = productRepository.findById(id)
              .orElseThrow(() -> new ProductNotFoundException(id));
        return entity.getDeletedAt() != null;
    }

    public void deleteById(Long id) {
        log.info("🔔 ELEMINANDO listing con ID {}...", id);
        Product product = getById(id);
        //this.productRepository.delete(product);
        product.setDeletedAt(LocalDateTime.now());
        product.setStatus(Status.DELETED);
        log.info("✅ Se ELEMINO el listing con ID {}...", id);
        this.productRepository.save(product);

    }


    // -- GET BY HASH
    public Product getBySku(String sku){
        Product product = this.productRepository.findActiveBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException());
        if (product.getDeletedAt() != null) {
            throw new ProductHasDeletedException("Producto no disponible o eliminado");
        }
        return product;
    }



}

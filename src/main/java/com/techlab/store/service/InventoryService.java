package com.techlab.store.service;

import com.techlab.store.entity.Product;
import com.techlab.store.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    @Autowired
    private final ProductRepository productRepository;
    
    @Transactional
    public boolean decreaseStock(Long productId, Integer quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);

        

        if (productOpt.isEmpty()) {
            return false;
        }

        Product product = productOpt.get();
        if (product.getStock() < quantity) {
            return false;
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
        return true;
    }
}
package com.techlab.store.dev.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techlab.store.entity.Product;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.utils.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductDevService {

    private final ProductRepository productRepository;
    private final StringUtils stringUtils;

    @Transactional
    public List<Product> saveAll(List<Product> products) {

        return productRepository.saveAll(products);
    }

}

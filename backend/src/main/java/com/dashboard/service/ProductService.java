package com.dashboard.service;

import com.dashboard.model.Product;
import com.dashboard.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getAll(Pageable pageable) {
        log.info("Fetching all products with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        return productRepository.findAll(pageable);
    }
}

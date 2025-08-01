package com.dashboard.controller;

import com.dashboard.dto.ProductResponseDTO;
import com.dashboard.model.Product;
import com.dashboard.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    // GET /api/products?page=0&size=10
    @GetMapping
    public Page<ProductResponseDTO> getAll(Pageable pageable) {
        return productService.getAll(pageable);
    }
}
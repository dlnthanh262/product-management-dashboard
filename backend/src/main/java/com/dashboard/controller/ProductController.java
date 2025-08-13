package com.dashboard.controller;

import com.dashboard.dto.BrandProductCountDTO;
import com.dashboard.dto.ProductRequestDTO;
import com.dashboard.dto.ProductResponseDTO;
import com.dashboard.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@PreAuthorize("hasRole('ADMIN')")
public class ProductController {
    private final ProductService productService;

    // GET /api/products?deleted=false&page=0&size=10
    @GetMapping
    public Page<ProductResponseDTO> getFilteredProducts(
        @RequestParam(defaultValue = "false") boolean deleted,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String brand,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        Pageable pageable) {
        return productService.getFilteredProducts(deleted, name, brand, minPrice, maxPrice, pageable);
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getOneById(id));
    }

    // POST /api/products
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    // PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> delete(@PathVariable Long id) {
        productService.markAsDeleted(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics/by-brand")
    public ResponseEntity<List<BrandProductCountDTO>> getProductCountByBrand() {
        return ResponseEntity.ok(productService.getProductCountByBrand());
    }
}
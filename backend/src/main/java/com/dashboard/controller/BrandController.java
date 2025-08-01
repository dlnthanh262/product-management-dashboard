package com.dashboard.controller;

import com.dashboard.dto.BrandRequestDTO;
import com.dashboard.dto.BrandResponseDTO;
import com.dashboard.model.Brand;
import com.dashboard.service.BrandService;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    // GET /api/brands?page=0&size=10
    @GetMapping
    public Page<BrandResponseDTO> getAll(Pageable pageable) {
        return brandService.getAll(pageable);
    }

    // GET /api/brands/{id}
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getOneById(id));
    }

    // POST /api/brands
    @PostMapping
    public ResponseEntity<BrandResponseDTO> create(@Valid @RequestBody BrandRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.create(request));
    }

    // PUT /api/brands/{id}
    @PutMapping("/{id}")
    public ResponseEntity<BrandResponseDTO> update(@PathVariable Long id, @Valid @RequestBody BrandRequestDTO request) {
        return ResponseEntity.ok(brandService.update(id, request));
    }

    // DELETE /api/brands/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Brand> delete(@PathVariable Long id) {
        brandService.markAsDeleted(id);
        return ResponseEntity.noContent().build();
    }
}
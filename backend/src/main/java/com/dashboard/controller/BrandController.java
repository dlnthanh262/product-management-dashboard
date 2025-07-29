package com.dashboard.controller;

import com.dashboard.model.Brand;
import com.dashboard.service.BrandService;
import jakarta.validation.Valid;
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

    @GetMapping
    public Page<Brand> getAllBrands(Pageable pageable) {
        return brandService.getAll(pageable);
    }

    @PostMapping
    public ResponseEntity<Object> createBrand(@Valid @RequestBody Brand inputBrand) {
        Brand brand = brandService.create(inputBrand);
        return ResponseEntity.status(HttpStatus.CREATED).body(brand);
    }
}
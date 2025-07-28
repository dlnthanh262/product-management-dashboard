package com.dashboard.controller;

import com.dashboard.dto.BrandDTO;
import com.dashboard.exception.ConflictException;
import com.dashboard.model.Brand;
import com.dashboard.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public List<Brand> getBrands() {
        return brandService.getAllBrands();
    }

    @PostMapping
    public ResponseEntity<Object> createBrand(@Valid @RequestBody BrandDTO brandDTO) {
        Brand brand = brandService.createBrand(brandDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(brand);
    }
}
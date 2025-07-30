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

    // GET /api/brands?page=0&size=10
    @GetMapping
    public Page<Brand> getAllBrands(Pageable pageable) {
        return brandService.getAll(pageable);
    }

    // GET /api/brands/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Long id) {
        Brand brand = brandService.getOneById(id);
        return ResponseEntity.status(HttpStatus.OK).body(brand);
    }

    // POST /api/brands
    @PostMapping
    public ResponseEntity<Object> createBrand(@Valid @RequestBody Brand inputBrand) {
        Brand brand = brandService.create(inputBrand);
        return ResponseEntity.status(HttpStatus.CREATED).body(brand);
    }

    // PUT /api/brands/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(
        @PathVariable Long id,
        @Valid @RequestBody Brand inputBrand
    ) {
        inputBrand.setId(id);
        Brand updatedBrand = brandService.update(inputBrand);
        return ResponseEntity.status(HttpStatus.OK).body(updatedBrand);
    }

    // DELETE /api/brands/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteBrand(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }
}
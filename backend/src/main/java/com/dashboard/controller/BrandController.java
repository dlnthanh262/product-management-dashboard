package com.dashboard.controller;

import com.dashboard.dto.BrandRequestDTO;
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
    public Page<Brand> getAll(Pageable pageable) {
        return brandService.getAll(pageable);
    }

    // GET /api/brands/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getById(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getOneById(id));
    }

    // POST /api/brands
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BrandRequestDTO request) {
        var brand = Brand.builder()
            .name(request.getName())
            .country(request.getCountry())
            .foundedYear(request.getFoundedYear())
            .website(request.getWebsite())
            .description(request.getDescription())
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.create(brand));
    }

    // PUT /api/brands/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Brand> update(@PathVariable Long id, @RequestBody Brand inputBrand) {
        inputBrand.setId(id);
        return ResponseEntity.ok(brandService.update(inputBrand));
    }

    // DELETE /api/brands/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Brand> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
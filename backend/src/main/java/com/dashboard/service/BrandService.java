package com.dashboard.service;

import com.dashboard.exception.ConflictException;
import com.dashboard.exception.InvalidFormatException;
import com.dashboard.exception.NotFoundException;
import com.dashboard.exception.ValidationException;
import com.dashboard.model.Brand;
import com.dashboard.repository.BrandRepository;
import com.dashboard.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public Page<Brand> getAll(Pageable pageable) {
        log.info("Fetching all brands with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        return brandRepository.findAll(pageable);
    }

    public Brand getOneById(Long id) {
        log.info("Start getting brand with ID: {}", id);

        if (id == null || id < 0) {
            throw new ValidationException("Invalid brand ID: " + id);
        }

        var optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isEmpty()) {
            log.error("Brand not found with ID: {}", id);
            throw new NotFoundException("Brand not found with ID: " + id);
        }

        var brand = optionalBrand.get();

        log.info("Brand get: {}", brand);
        return brand;
    }

    public Brand create(Brand inputBrand) {
        log.info("Inserting new brand with name: {}", inputBrand.getName());

        if (brandRepository.existsByName(inputBrand.getName())) {
            throw new ConflictException("Brand already exists with name: " + inputBrand.getName());
        }

        Brand brandToCreate = Brand.builder()
            .name(inputBrand.getName())
            .country(inputBrand.getCountry())
            .foundedYear(inputBrand.getFoundedYear())
            .website(inputBrand.getWebsite())
            .description(inputBrand.getDescription())
            .build();

        var createdBrand = brandRepository.save(brandToCreate);

        log.info("Brand created: {}", createdBrand);
        return createdBrand;
    }

    public Brand update(Brand inputBrand) {
        log.info("Updating brand with ID: {}", inputBrand.getId());

        var inputBrandId = inputBrand.getId();
        if (inputBrandId == null || inputBrandId < 0) {
            log.warn("Invalid brand ID: {}", inputBrandId);
            throw new InvalidFormatException("Invalid brand ID: " + inputBrandId);
        }

        var optionalExistingBrand = brandRepository.findById(inputBrandId);
        if (optionalExistingBrand.isEmpty()) {
            log.error("Brand not found with ID: {}", inputBrandId);
            throw new NotFoundException("Brand not found with ID: " + inputBrandId);
        }

        var inputBrandName = inputBrand.getName();
        if (inputBrandName == null || inputBrandName.trim().isBlank()) {
            log.warn("Empty brand name provided for update");
            throw new ValidationException("Brand name must not be empty");
        }

        var optionalBrandByName = brandRepository.findByName(inputBrandName);
        if (optionalBrandByName.isPresent() && !optionalBrandByName.get().getId().equals(inputBrandId)) {
            log.warn("Conflict: Brand name '{}' already exists", inputBrandName);
            throw new ConflictException("Brand name already exists: " + inputBrandName);
        }

        var brandToUpdate = optionalExistingBrand.get();
        brandToUpdate.setName(inputBrandName);
        brandToUpdate.setCountry(inputBrand.getCountry());
        brandToUpdate.setFoundedYear(inputBrand.getFoundedYear());
        brandToUpdate.setWebsite(inputBrand.getWebsite());
        brandToUpdate.setDescription(inputBrand.getDescription());

        var updatedBrand = brandRepository.save(brandToUpdate);

        log.info("Brand updated: {}", updatedBrand);
        return updatedBrand;
    }

    public void delete(Long id) {
        log.info("Deleting brand with ID: {}", id);

        if (id == null || id < 0) {
            log.warn("Invalid brand ID: {}", id);
            throw new ValidationException("Invalid brand ID: " + id);
        }

        var optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isEmpty()) {
            log.error("Brand not found with ID: {}", id);
            throw new NotFoundException("Brand not found with ID: " + id);
        }

        var brand = optionalBrand.get();

        boolean hasProducts = productRepository.existsByBrand(brand);
        if (hasProducts) {
            log.warn("Conflict: Brand is used by one or more products");
            throw new ConflictException("Cannot delete brand: it is used by one or more products");
        }

        log.info("Brand deleted");
        brandRepository.deleteById(id);
    }
}

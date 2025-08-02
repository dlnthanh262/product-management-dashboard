package com.dashboard.service;

import com.dashboard.dto.BrandRequestDTO;
import com.dashboard.dto.BrandResponseDTO;
import com.dashboard.exception.ConflictException;
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

    public Page<BrandResponseDTO> getAllByDeleted(boolean deleted, Pageable pageable) {
        log.info("Fetching brands with deleted = {}. Page {}, size {}", deleted, pageable.getPageNumber(), pageable.getPageSize());
        return brandRepository.findByDeleted(deleted, pageable)
            .map(this::mapToResponseDTO);
    }

    public BrandResponseDTO getOneById(Long id) {
        log.info("Start getting brand with ID: {}", id);

        if (id == null || id < 0) {
            log.warn("Invalid brand ID: {}", id);
            throw new ValidationException("Invalid brand ID: " + id);
        }

        var optionalBrand = brandRepository.findByIdAndDeleted(id, false);
        if (optionalBrand.isEmpty()) {
            log.error("Brand not found with ID: {}", id);
            throw new NotFoundException("Brand not found with ID: " + id);
        }

        var brand = optionalBrand.get();

        log.info("Get brand: {} (id: {})", brand.getName(), brand.getId());
        return mapToResponseDTO(brand);
    }

    public BrandResponseDTO create(BrandRequestDTO brandRequestDTO) {
        log.info("Inserting new brand with name: {}", brandRequestDTO.getName());

        if (brandRepository.existsByNameAndDeleted(brandRequestDTO.getName(), false)) {
            throw new ConflictException("Brand already exists with name: " + brandRequestDTO.getName());
        }

        Brand brandToCreate = Brand.builder()
            .name(brandRequestDTO.getName())
            .country(brandRequestDTO.getCountry())
            .foundedYear(brandRequestDTO.getFoundedYear())
            .website(brandRequestDTO.getWebsite())
            .description(brandRequestDTO.getDescription())
            .deleted(false)
            .build();

        var createdBrand = brandRepository.save(brandToCreate);

        log.info("Brand created: {} (id: {})", createdBrand.getName(), createdBrand.getId());
        return mapToResponseDTO(createdBrand);
    }

    public BrandResponseDTO update(Long id, BrandRequestDTO brandRequestDTO) {
        log.info("Updating brand with ID: {}", id);

        if (id == null || id < 0) {
            log.warn("Invalid brand ID: {}", id);
            throw new ValidationException("Invalid brand ID: " + id);
        }

        var optionalExistingBrand = brandRepository.findByIdAndDeleted(id, false);
        if (optionalExistingBrand.isEmpty()) {
            log.error("Brand not found with ID: {}", id);
            throw new NotFoundException("Brand not found with ID: " + id);
        }

        var optionalBrandByName = brandRepository.findByNameAndDeleted(brandRequestDTO.getName(), false);
        if (optionalBrandByName.isPresent() && !optionalBrandByName.get().getId().equals(id)) {
            log.warn("Conflict: Brand name '{}' already exists", brandRequestDTO.getName());
            throw new ConflictException("Brand name already exists: " + brandRequestDTO.getName());
        }

        var brandToUpdate = optionalExistingBrand.get();
        brandToUpdate.setName(brandRequestDTO.getName());
        brandToUpdate.setCountry(brandRequestDTO.getCountry());
        brandToUpdate.setFoundedYear(brandRequestDTO.getFoundedYear());
        brandToUpdate.setWebsite(brandRequestDTO.getWebsite());
        brandToUpdate.setDescription(brandRequestDTO.getDescription());

        var updatedBrand = brandRepository.save(brandToUpdate);

        log.info("Brand updated: {} (id {})", updatedBrand.getName(), updatedBrand.getId());
        return mapToResponseDTO(updatedBrand);
    }

    public void markAsDeleted(Long id) {
        log.info("Deleting brand with ID: {}", id);

        if (id == null || id < 0) {
            log.warn("Invalid brand ID: {}", id);
            throw new ValidationException("Invalid brand ID: " + id);
        }

        var optionalBrand = brandRepository.findByIdAndDeleted(id, false);
        if (optionalBrand.isEmpty()) {
            log.error("Brand not found with ID: {}", id);
            throw new NotFoundException("Brand not found with ID: " + id);
        }

        var brand = optionalBrand.get();

        boolean hasProducts = productRepository.existsByBrandAndDeleted(brand, false);
        if (hasProducts) {
            log.warn("Conflict: Brand is used by one or more products");
            throw new ConflictException("Cannot delete brand: it is used by one or more products");
        }

        log.info("Brand deleted");
        brandRepository.setDeletedTrueById(id);
    }

    private BrandResponseDTO mapToResponseDTO(Brand brand) {
        return new BrandResponseDTO(
            brand.getId(),
            brand.getName(),
            brand.getCountry(),
            brand.getFoundedYear(),
            brand.getWebsite(),
            brand.getDescription()
        );
    }
}

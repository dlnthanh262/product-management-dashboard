package com.dashboard.service;

import com.dashboard.dto.ProductRequestDTO;
import com.dashboard.dto.ProductResponseDTO;
import com.dashboard.exception.ConflictException;
import com.dashboard.exception.NotFoundException;
import com.dashboard.exception.ValidationException;
import com.dashboard.model.Brand;
import com.dashboard.model.Product;
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
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    private Product getOrThrowProduct(Long id) {
        return productRepository.findByIdAndDeleted(id, false)
            .orElseThrow(() -> {
                log.error("Product not found with ID: {}", id);
                throw new NotFoundException("Product not found with ID: " + id);
            });
    }

    private Brand getOrThrowBrand(Long id) {
        return brandRepository.findByIdAndDeleted(id, false)
            .orElseThrow(() -> {
                log.error("Brand not found with ID: {}", id);
                throw new NotFoundException("Brand not found with ID: " + id);
            });
    }

    private void validateProductId(Long id) {
        if (id == null || id < 0) {
            log.warn("Invalid product ID: {}", id);
            throw new ValidationException("Invalid product ID: " + id);
        }
    }

    public Page<ProductResponseDTO> getAllByDeleted(boolean deleted, Pageable pageable) {
        log.info("Fetching product with deleted = {}. Page {}, size {}", deleted, pageable.getPageNumber(), pageable.getPageSize());
        return productRepository.findByDeleted(deleted, pageable)
            .map(this::mapToResponseDTO);
    }

    public ProductResponseDTO getOneById(Long id) {
        log.info("Start getting product with ID: {}", id);

        validateProductId(id);

        var product = getOrThrowProduct(id);

        log.info("Get product: {} (id: {})", product.getName(), product.getId());
        return mapToResponseDTO(product);
    }

    public ProductResponseDTO create(ProductRequestDTO productRequestDTO) {
        log.info("Inserting new product with name: {} of brand ID {}", productRequestDTO.getName(), productRequestDTO.getBrandId());

        var brand = getOrThrowBrand(productRequestDTO.getBrandId());

        if (productRepository.existsByNameAndBrandAndDeletedFalse(productRequestDTO.getName(), brand)) {
            log.error("Brand already has one Product with name: {}", productRequestDTO.getName());
            throw new ConflictException("Brand already has one Product with name: " + productRequestDTO.getName());
        }

        Product productToCreate = Product.builder()
            .name(productRequestDTO.getName())
            .brand(brand)
            .quantity(productRequestDTO.getQuantity())
            .price(productRequestDTO.getPrice())
            .deleted(false)
            .build();

        var createdProduct = productRepository.save(productToCreate);

        log.info("Product created: {} (id: {})", createdProduct.getName(), createdProduct.getId());
        return mapToResponseDTO(createdProduct);
    }

    public ProductResponseDTO update(Long id, ProductRequestDTO productRequestDTO) {
        log.info("Updating product with ID: {}", id);

        validateProductId(id);
        var existingProduct = getOrThrowProduct(id);
        var existingBrand = getOrThrowBrand(productRequestDTO.getBrandId());

        var optionalProductByNameAndBrand = productRepository.findByBrandAndNameAndDeletedFalse(existingBrand, productRequestDTO.getName());
        if (optionalProductByNameAndBrand.isPresent() && !optionalProductByNameAndBrand.get().getId().equals(id)) {
            log.warn("Conflict: Brand already has one Product with name {}", productRequestDTO.getName());
            throw new ConflictException("Brand already has one Product with name: " + productRequestDTO.getName());
        }

        log.debug("Before update: {}", existingProduct);

        existingProduct.setName(productRequestDTO.getName());
        existingProduct.setBrand(existingBrand);
        existingProduct.setQuantity(productRequestDTO.getQuantity());
        existingProduct.setPrice(productRequestDTO.getPrice());

        var updatedProduct = productRepository.save(existingProduct);

        log.debug("After update: {}", updatedProduct);
        log.info("Product updated: {} (id {})", updatedProduct.getName(), updatedProduct.getId());
        return mapToResponseDTO(updatedProduct);
    }

    public void markAsDeleted(Long id) {
        log.info("Deleting product with ID: {}", id);

        validateProductId(id);
        var product = getOrThrowProduct(id);

        productRepository.setDeletedTrueById(product.getId());
        log.info("Product ID {} deleted", id);
    }

    private ProductResponseDTO mapToResponseDTO(Product product) {
        return new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getBrand().getName(),
            product.getQuantity(),
            product.getPrice()
        );
    }
}

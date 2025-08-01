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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    public Page<ProductResponseDTO> getAll(Pageable pageable) {
        log.info("Fetching all products with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        return productRepository.findAll(pageable)
            .map(this::mapToDTO);
    }

    public ProductResponseDTO getOneById(Long id) {
        log.info("Start getting product with ID: {}", id);

        if (id == null || id < 0) {
            log.warn("Invalid product ID: {}", id);
            throw new ValidationException("Invalid product ID: " + id);
        }

        var optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            log.error("Product not found with ID: {}", id);
            throw new NotFoundException("Product not found with ID: " + id);
        }

        var product = optionalProduct.get();

        log.info("Get product: {} (id: {})", product.getName(), product.getId());
        return mapToDTO(product);
    }

    public ProductResponseDTO create(ProductRequestDTO productRequestDTO) {
        log.info("Inserting new product with name: {} of brand ID {}", productRequestDTO.getName(), productRequestDTO.getBrandId());

        var optionalBrand = brandRepository.findById(productRequestDTO.getBrandId());
        if (optionalBrand.isEmpty()) {
            log.error("Brand not found with ID: {}", productRequestDTO.getBrandId());
            throw new NotFoundException("Brand not found with ID: " + productRequestDTO.getBrandId());
        }

        var brand = optionalBrand.get();
        if (productRepository.existsByNameAndBrand(productRequestDTO.getName(), brand)) {
            log.error("Brand already has one Product with name: {}", productRequestDTO.getName());
            throw new ConflictException("Brand already has one Product with name: " + productRequestDTO.getName());
        }

        Product productToCreate = Product.builder()
            .name(productRequestDTO.getName())
            .brand(brand)
            .quantity(productRequestDTO.getQuantity())
            .price(productRequestDTO.getPrice())
            .build();

        var createdProduct = productRepository.save(productToCreate);

        log.info("Product created: {} (id: {})", createdProduct.getName(), createdProduct.getId());
        return mapToDTO(createdProduct);
    }

    private ProductResponseDTO mapToDTO(Product product) {
        return new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getBrand().getName(),
            product.getQuantity(),
            product.getPrice()
        );
    }
}

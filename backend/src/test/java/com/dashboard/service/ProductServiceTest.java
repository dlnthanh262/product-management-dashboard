package com.dashboard.service;

import com.dashboard.dto.ProductRequestDTO;
import com.dashboard.exception.ConflictException;
import com.dashboard.exception.NotFoundException;
import com.dashboard.exception.ValidationException;
import com.dashboard.model.Brand;
import com.dashboard.model.Product;
import com.dashboard.repository.BrandRepository;
import com.dashboard.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    private static final String PRODUCT_NAME = "iPhone 16 Pro Max";
    private static final Long VALID_PRODUCT_ID = 1L;
    private static final Long NULL_PRODUCT_ID = null;
    private static final Long NEGATIVE_PRODUCT_ID = -2L;
    private static final Long NOT_FOUND_PRODUCT_ID = 16437L;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private BrandRepository brandRepository;
    private ProductService productService;
    private Brand brand;
    private Product product;
    private ProductRequestDTO productRequestDTO;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, brandRepository);
        brand = Brand.builder().id(1L).name("Apple").deleted(false).build();
        product = Product.builder()
            .id(VALID_PRODUCT_ID)
            .brand(brand)
            .name(PRODUCT_NAME)
            .price(1000.0)
            .quantity(100)
            .deleted(false)
            .build();
        productRequestDTO = new ProductRequestDTO(PRODUCT_NAME, brand.getId(), 100, 1000.0);
    }

    @Test
    void givenDeletedIsFalse_whenGetFilteredProducts_thenReturnNonemptyList() {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            products.add(Product.builder()
                .id((long) (i + 1))
                .name("ProductName" + (i + 1))
                .brand(Brand.builder().id(1L).name("BrandName").build())
                .deleted(false)
                .build());
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.searchProducts(false, "", "", null, null, pageable)).thenReturn(productPage);

        var result = productService.getFilteredProducts(false, "", "", null, null, pageable);

        assertEquals(10, result.getNumberOfElements());
        assertEquals("ProductName1", result.getContent().get(0).getName());
        assertEquals("ProductName10", result.getContent().get(9).getName());
    }

    @Test
    void givenDeletedIsFalse_whenGetFilteredProducts_thenReturnEmptyList() {
        List<Product> products = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.searchProducts(false, "", "", null, null, pageable)).thenReturn(productPage);

        var results = productService.getFilteredProducts(false, "", "", null, null, pageable);

        assertEquals(0, results.getNumberOfElements());
    }

    @Test
    void givenDeletedIsTrue_whenGetFilteredProducts_thenReturnNonemptyList() {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            products.add(Product.builder()
                .id((long) (i + 1))
                .name("ProductName" + (i + 1))
                .brand(Brand.builder().id(1L).name("BrandName").build())
                .deleted(true)
                .build());
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.searchProducts(true, "", "", null, null, pageable)).thenReturn(productPage);

        var results = productService.getFilteredProducts(true, "", "", null, null, pageable);

        assertEquals(10, results.getNumberOfElements());
        assertEquals("ProductName1", results.getContent().get(0).getName());
        assertEquals("ProductName10", results.getContent().get(9).getName());
    }

    @Test
    void givenInvalidId_whenGetOneById_thenThrowValidationException() {
        assertThatThrownBy(() -> productService.getOneById(NULL_PRODUCT_ID)).isInstanceOf(ValidationException.class).hasMessageContaining("Invalid product ID: " + NULL_PRODUCT_ID);
        assertThatThrownBy(() -> productService.getOneById(NEGATIVE_PRODUCT_ID)).isInstanceOf(ValidationException.class).hasMessageContaining("Invalid product ID: " + NEGATIVE_PRODUCT_ID);
    }

    @Test
    void givenNonExistentId_whenGetOneById_thenThrowNotFoundException() {
        when(productRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.getOneById(NOT_FOUND_PRODUCT_ID)).isInstanceOf(NotFoundException.class).hasMessageContaining("Product not found with ID: " + NOT_FOUND_PRODUCT_ID);
    }

    @Test
    void givenExistentId_whenGetOneById_thenReturnBrand() {
        when(productRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(product));

        var result = productService.getOneById(VALID_PRODUCT_ID);

        assertNotNull(result);
        assertEquals(VALID_PRODUCT_ID, result.getId());
    }

    @Test
    void givenNotFoundBrand_whenCreate_thenThrowNotFoundException() {
        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.create(productRequestDTO)).isInstanceOf(NotFoundException.class).hasMessageContaining("Brand not found with ID: " + productRequestDTO.getBrandId());
    }

    @Test
    void givenExistentProductNameInBrand_whenCreate_thenThrowConflictException() {
        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(brand));
        when(productRepository.existsByNameAndBrandAndDeletedFalse(Mockito.anyString(), Mockito.any(Brand.class))).thenReturn(true);
        assertThatThrownBy(() -> productService.create(productRequestDTO)).isInstanceOf(ConflictException.class).hasMessageContaining("Brand already has one Product with name: " + productRequestDTO.getName());
    }

    @Test
    void givenValidInput_whenCreate_thenReturnCreatedProduct() {
        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(brand));
        when(productRepository.existsByNameAndBrandAndDeletedFalse(Mockito.anyString(), Mockito.any(Brand.class))).thenReturn(false);
        when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);

        var productResponseDTO = productService.create(productRequestDTO);

        assertEquals(PRODUCT_NAME, productResponseDTO.getName());
    }

    @Test
    void givenInvalidProductId_whenUpdate_thenThrowValidationException() {
        assertThatThrownBy(() -> productService.update(NULL_PRODUCT_ID, productRequestDTO)).isInstanceOf(ValidationException.class).hasMessageContaining("Invalid product ID: " + NULL_PRODUCT_ID);
        assertThatThrownBy(() -> productService.update(NEGATIVE_PRODUCT_ID, productRequestDTO)).isInstanceOf(ValidationException.class).hasMessageContaining("Invalid product ID: " + NEGATIVE_PRODUCT_ID);
    }

    @Test
    void givenNonExistentProductId_whenUpdate_thenThrowNotFoundException() {
        when(productRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.update(NOT_FOUND_PRODUCT_ID, productRequestDTO)).isInstanceOf(NotFoundException.class).hasMessageContaining("Product not found with ID: " + NOT_FOUND_PRODUCT_ID);
    }

    @Test
    void givenNonExistentBrandId_whenUpdate_thenThrowNotFoundException() {
        when(productRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(product));
        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.update(NOT_FOUND_PRODUCT_ID, productRequestDTO)).isInstanceOf(NotFoundException.class).hasMessageContaining("Brand not found with ID: " + productRequestDTO.getBrandId());
    }

    @Test
    void givenConflictProductName_whenUpdate_thenThrowConflictException() {
        var nameToUpdate = "iPhone 11";
        var conflictProduct = product;
        conflictProduct.setId(VALID_PRODUCT_ID + 1);
        productRequestDTO.setName(nameToUpdate);

        when(productRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(product));
        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(brand));
        when(productRepository.findByBrandAndNameAndDeletedFalse(Mockito.any(Brand.class), Mockito.anyString())).thenReturn(Optional.of(conflictProduct));

        assertThatThrownBy(() -> productService.update(VALID_PRODUCT_ID, productRequestDTO)).isInstanceOf(ConflictException.class).hasMessageContaining("Brand already has one Product with name: " + nameToUpdate);
    }

    @Test
    void givenValidInput_whenUpdate_thenReturnUpdatedProduct() {
        var nameToUpdate = "iPhone 11";
        productRequestDTO.setName(nameToUpdate);

        when(productRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(product));
        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(brand));
        when(productRepository.findByBrandAndNameAndDeletedFalse(Mockito.any(Brand.class), Mockito.anyString())).thenReturn(Optional.empty());
        when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);

        var updatedProduct= productService.update(VALID_PRODUCT_ID, productRequestDTO);

        assertEquals(VALID_PRODUCT_ID, updatedProduct.getId());
        assertEquals(nameToUpdate, updatedProduct.getName());
        verify(productRepository).save(Mockito.any(Product.class));
    }

    @Test
    void givenInvalidId_whenDelete_thenThrowValidationException() {
        assertThatThrownBy(() -> productService.markAsDeleted(NULL_PRODUCT_ID)).isInstanceOf(ValidationException.class).hasMessageContaining("Invalid product ID: " + NULL_PRODUCT_ID);
        assertThatThrownBy(() -> productService.markAsDeleted(NEGATIVE_PRODUCT_ID)).isInstanceOf(ValidationException.class).hasMessageContaining("Invalid product ID: " + NEGATIVE_PRODUCT_ID);
    }

    @Test
    void givenNonExistentId_whenDelete_thenThrowNotFoundException() {
        when(productRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.markAsDeleted(NOT_FOUND_PRODUCT_ID)).isInstanceOf(NotFoundException.class).hasMessageContaining("Product not found with ID: " + NOT_FOUND_PRODUCT_ID);
    }

    @Test
    void givenExistentId_whenDelete_thenSuccess() {
        doNothing().when(productRepository).setDeletedTrueById(Mockito.anyLong());
        when(productRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> productService.markAsDeleted(VALID_PRODUCT_ID));
    }
}
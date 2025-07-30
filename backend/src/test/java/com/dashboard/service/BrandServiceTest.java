package com.dashboard.service;

import com.dashboard.exception.ConflictException;
import com.dashboard.exception.NotFoundException;
import com.dashboard.exception.ValidationException;
import com.dashboard.model.Brand;
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
class BrandServiceTest {
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private ProductRepository productRepository;
    private BrandService brandService;

    @BeforeEach
    void setUp() {
        brandService = new BrandService(brandRepository, productRepository);
    }

    @Test
    void givenTableHasRecords_whenGetAllBrands_thenReturnNonemptyList() {
        List<Brand> brands = new ArrayList<>();
        for (int i = 0; i < 19; i++) {
            brands.add(Brand.builder().id((long) (i + 1)).name("BrandName" + (i + 1)).build());
        }

        Pageable pageable1 = PageRequest.of(0, 10);
        Pageable pageable2 = PageRequest.of(1, 10);

        List<Brand> pageContent1 = brands.subList(0, 10);
        List<Brand> pageContent2 = brands.subList(10, 19);
        Page<Brand> brandPage1 = new PageImpl<>(pageContent1);
        Page<Brand> brandPage2 = new PageImpl<>(pageContent2);

        when(brandRepository.findAll(pageable1)).thenReturn(brandPage1);
        when(brandRepository.findAll(pageable2)).thenReturn(brandPage2);

        var result1 = brandService.getAll(pageable1);
        var result2 = brandService.getAll(pageable2);

        assertEquals(10, result1.getNumberOfElements());
        assertEquals("BrandName1", result1.getContent().get(0).getName());
        assertEquals("BrandName10", result1.getContent().get(9).getName());

        assertEquals(9, result2.getNumberOfElements());
        assertEquals("BrandName11", result2.getContent().get(0).getName());
        assertEquals("BrandName19", result2.getContent().get(8).getName());
    }

    @Test
    void givenEmptyTable_whenGetAllBrands_thenReturnEmptyList() {
        List<Brand> brands = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Brand> brandPage = new PageImpl<>(brands);

        when(brandRepository.findAll(pageable)).thenReturn(brandPage);

        var results = brandService.getAll(pageable);

        assertEquals(0, results.getNumberOfElements());
    }

    @Test
    void givenInvalidId_whenGetOneById_thenThrowValidationException() {
        Long nullId = null;
        Long negativeId = -2L;

        assertThatThrownBy(() -> brandService.getOneById(nullId))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid brand ID: " + nullId);

        assertThatThrownBy(() -> brandService.getOneById(negativeId))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid brand ID: " + negativeId);
    }

    @Test
    void givenNonExistentId_whenGetOneById_thenThrowNotFoundException() {
        Long id = 16787L;

        when(brandRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.getOneById(id))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Brand not found with ID: " + id);
    }

    @Test
    void givenExistentId_whenGetOneById_thenReturnBrand() {
        Long id = 1L;

        when(brandRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(Brand.builder().id(id).build()));

        var result = brandService.getOneById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void givenExistentName_whenCreate_thenThrowConflictException() {
        var inputBrand = Brand.builder().name("Apple").build();

        when(brandRepository.existsByName(Mockito.anyString())).thenReturn(true);

        assertThatThrownBy(() -> brandService.create(inputBrand))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("Brand already exists with name: " + inputBrand.getName());
    }

    @Test
    void givenValidInputBrand_whenCreate_thenReturnCreatedBrand() {
        var name = "Apple";
        var inputBrand = Brand.builder().name(name).build();

        when(brandRepository.existsByName(Mockito.anyString())).thenReturn(false);
        when(brandRepository.save(Mockito.any(Brand.class))).thenReturn(Brand.builder().id(1L).name(name).build());

        var createdBrand = brandService.create(inputBrand);

        assertEquals(name, createdBrand.getName());
        verify(brandRepository).save(Mockito.any(Brand.class));
    }

    @Test
    void givenInvalidId_whenUpdate_thenThrowValidationException() {
        Long nullId = null;
        Long negativeId = -4L;

        assertThatThrownBy(() -> brandService.update(Brand.builder().id(nullId).build()))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid brand ID: " + nullId);

        assertThatThrownBy(() -> brandService.update(Brand.builder().id(negativeId).build()))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid brand ID: " + negativeId);
    }

    @Test
    void givenNonExistentId_whenUpdate_thenThrowNotFoundException() {
        Long id = 14378L;

        when(brandRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.update(Brand.builder().id(id).build()))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Brand not found with ID: " + id);
    }

    @Test
    void givenConflictName_whenUpdate_thenThrowConflictException() {
        Long id = 10L;
        String name = "HP";

        when(brandRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(Brand.builder().id(id).build()));
        when(brandRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(Brand.builder().id(2L).build()));

        assertThatThrownBy(() -> brandService.update(Brand.builder().id(id).name(name).build()))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("Brand name already exists: " + name);
    }

    @Test
    void givenValidInputBrand_whenUpdate_thenReturnUpdatedBrand() {
        var brand = Brand.builder().id(1L).name("HP").build();

        when(brandRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(Brand.builder().id(1L).build()));
        when(brandRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
        when(brandRepository.save(Mockito.any(Brand.class))).thenReturn(brand);

        var updatedBrand = brandService.update(brand);

        assertEquals(1L, updatedBrand.getId());
        assertEquals("HP", updatedBrand.getName());
        verify(brandRepository).save(Mockito.any(Brand.class));
    }

    @Test
    void givenInvalidId_whenDelete_thenThrowValidationException() {
        Long nullId = null;
        Long negativeId = -4L;

        assertThatThrownBy(() -> brandService.delete(nullId))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid brand ID: " + nullId);

        assertThatThrownBy(() -> brandService.delete(negativeId))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid brand ID: " + negativeId);
    }

    @Test
    void givenNonExistentId_whenDelete_thenThrowNotFoundException() {
        Long id = 16787L;

        when(brandRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.delete(id))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Brand not found with ID: " + id);
    }

    @Test
    void givenBrandUsedByProducts_whenDelete_thenThrowConflictException() {
        Long id = 16787L;
        var brand = Brand.builder().id(id).name("Apple").build();

        when(brandRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(brand));
        when(productRepository.existsByBrand(Mockito.any(Brand.class))).thenReturn(true);

        assertThatThrownBy(() -> brandService.delete(id))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("Cannot delete brand: it is used by one or more products");
    }

    @Test
    void givenBrandNotUsedByProducts_whenDelete_thenSuccess() {
        Long id = 16787L;
        var brand = Brand.builder().id(id).name("Apple").build();

        when(brandRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(brand));
        when(productRepository.existsByBrand(Mockito.any(Brand.class))).thenReturn(false);
        doNothing().when(brandRepository).deleteById(Mockito.anyLong());

        assertDoesNotThrow(() -> brandService.delete(16787L));
    }
}
package com.dashboard.service;

import com.dashboard.dto.BrandRequestDTO;
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

        when(brandRepository.findByDeleted(false, pageable1)).thenReturn(brandPage1);
        when(brandRepository.findByDeleted(false, pageable2)).thenReturn(brandPage2);

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

        when(brandRepository.findByDeleted(false, pageable)).thenReturn(brandPage);

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

        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.getOneById(id))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Brand not found with ID: " + id);
    }

    @Test
    void givenExistentId_whenGetOneById_thenReturnBrand() {
        Long id = 1L;

        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(Brand.builder().id(id).build()));

        var result = brandService.getOneById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void givenExistentName_whenCreate_thenThrowConflictException() {
        var brandRequestDTO = new BrandRequestDTO("Apple", "country", 1900, "https://apple.com", "description");

        when(brandRepository.existsByNameAndDeleted(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(true);

        assertThatThrownBy(() -> brandService.create(brandRequestDTO))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("Brand already exists with name: " + brandRequestDTO.getName());
    }

    @Test
    void givenValidInputBrand_whenCreate_thenReturnCreatedBrand() {
        var name = "Apple";
        var brandRequestDTO = new BrandRequestDTO(name, "country", 1900, "https://apple.com", "description");

        when(brandRepository.existsByNameAndDeleted(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(false);
        when(brandRepository.save(Mockito.any(Brand.class))).thenReturn(Brand.builder().id(1L).name(name).build());

        var brandResponseDTO = brandService.create(brandRequestDTO);

        assertEquals(name, brandResponseDTO.getName());
        verify(brandRepository).save(Mockito.any(Brand.class));
    }

    @Test
    void givenInvalidId_whenUpdate_thenThrowValidationException() {
        Long nullId = null;
        Long negativeId = -4L;

        assertThatThrownBy(() -> brandService.update(nullId, new BrandRequestDTO()))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid brand ID: " + nullId);

        assertThatThrownBy(() -> brandService.update(negativeId, new BrandRequestDTO()))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid brand ID: " + negativeId);
    }

    @Test
    void givenNonExistentId_whenUpdate_thenThrowNotFoundException() {
        Long id = 14378L;

        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.update(id, new BrandRequestDTO()))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Brand not found with ID: " + id);
    }

    @Test
    void givenConflictName_whenUpdate_thenThrowConflictException() {
        Long id = 10L;
        var name = "HP";
        var brandRequestDTO = new BrandRequestDTO(name, "country", 1900, "https://apple.com", "description");


        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(Brand.builder().id(id).build()));
        when(brandRepository.findByNameAndDeleted(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(Optional.of(Brand.builder().id(2L).build()));

        assertThatThrownBy(() -> brandService.update(id, brandRequestDTO))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("Brand name already exists: " + name);
    }

    @Test
    void givenValidInputBrand_whenUpdate_thenReturnUpdatedBrand() {
        Long id = 10L;
        var name = "HP";
        var brandRequestDTO = new BrandRequestDTO(name, "country", 1900, "https://apple.com", "description");

        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(Brand.builder().id(1L).build()));
        when(brandRepository.findByNameAndDeleted(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(Optional.empty());
        when(brandRepository.save(Mockito.any(Brand.class))).thenReturn(Brand.builder().id(id).name(name).build());

        var updatedBrand = brandService.update(id, brandRequestDTO);

        assertEquals(id, updatedBrand.getId());
        assertEquals(name, updatedBrand.getName());
        verify(brandRepository).save(Mockito.any(Brand.class));
    }

    @Test
    void givenInvalidId_whenDelete_thenThrowValidationException() {
        Long nullId = null;
        Long negativeId = -4L;

        assertThatThrownBy(() -> brandService.markAsDeleted(nullId))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid brand ID: " + nullId);

        assertThatThrownBy(() -> brandService.markAsDeleted(negativeId))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid brand ID: " + negativeId);
    }

    @Test
    void givenNonExistentId_whenDelete_thenThrowNotFoundException() {
        Long id = 16787L;

        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.markAsDeleted(id))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Brand not found with ID: " + id);
    }

    @Test
    void givenBrandUsedByProducts_whenDelete_thenThrowConflictException() {
        Long id = 16787L;
        var brand = Brand.builder().id(id).name("Apple").build();

        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(brand));
        when(productRepository.existsByBrandAndDeleted(Mockito.any(Brand.class), Mockito.anyBoolean())).thenReturn(true);

        assertThatThrownBy(() -> brandService.markAsDeleted(id))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("Cannot delete brand: it is used by one or more products");
    }

    @Test
    void givenBrandNotUsedByProducts_whenDelete_thenSuccess() {
        Long id = 16787L;
        var name = "HP";
        var brandRequestDTO = new BrandRequestDTO(name, "country", 1900, "https://apple.com", "description");

        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(Brand.builder().build()));
        when(productRepository.existsByBrandAndDeleted(Mockito.any(Brand.class), Mockito.anyBoolean())).thenReturn(false);
        doNothing().when(brandRepository).setDeletedTrueById(Mockito.anyLong());

        assertDoesNotThrow(() -> brandService.markAsDeleted(16787L));
    }
}
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
    private static final String NAME = "Apple";
    private static final Long VALID_ID = 1L;
    private static final Long NULL_ID = null;
    private static final Long NEGATIVE_ID = -2L;
    private static final Long NOT_FOUND_ID = 16437L;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private ProductRepository productRepository;
    private BrandService brandService;
    private BrandRequestDTO brandRequestDTO;

    @BeforeEach
    void setUp() {
        brandService = new BrandService(brandRepository, productRepository);
        brandRequestDTO = new BrandRequestDTO(NAME, "country", 1900, "https://apple.com", "description");
    }

    @Test
    void givenDeletedIsFalse_whenGetAllBrands_thenReturnNonemptyList() {
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

        var result1 = brandService.getAllByDeleted(false, pageable1);
        var result2 = brandService.getAllByDeleted(false,pageable2);

        assertEquals(10, result1.getNumberOfElements());
        assertEquals("BrandName1", result1.getContent().get(0).getName());
        assertEquals("BrandName10", result1.getContent().get(9).getName());

        assertEquals(9, result2.getNumberOfElements());
        assertEquals("BrandName11", result2.getContent().get(0).getName());
        assertEquals("BrandName19", result2.getContent().get(8).getName());
    }

    @Test
    void givenDeletedIsFalse_whenGetAllBrands_thenReturnEmptyList() {
        List<Brand> brands = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Brand> brandPage = new PageImpl<>(brands);

        when(brandRepository.findByDeleted(false, pageable)).thenReturn(brandPage);

        var results = brandService.getAllByDeleted(false, pageable);

        assertEquals(0, results.getNumberOfElements());
    }

    @Test
    void givenDeletedIsTrue_whenGetAllByDeleted_thenReturnNonemptyList() {
        List<Brand> brands = new ArrayList<>();
        for (int i = 0; i < 19; i++) {
            brands.add(Brand.builder().id((long) (i + 1)).name("BrandName" + (i + 1)).deleted(true).build());
        }

        Pageable pageable1 = PageRequest.of(0, 10);
        Pageable pageable2 = PageRequest.of(1, 10);

        List<Brand> pageContent1 = brands.subList(0, 10);
        List<Brand> pageContent2 = brands.subList(10, 19);
        Page<Brand> brandPage1 = new PageImpl<>(pageContent1);
        Page<Brand> brandPage2 = new PageImpl<>(pageContent2);

        when(brandRepository.findByDeleted(true, pageable1)).thenReturn(brandPage1);
        when(brandRepository.findByDeleted(true, pageable2)).thenReturn(brandPage2);

        var result1 = brandService.getAllByDeleted(true, pageable1);
        var result2 = brandService.getAllByDeleted(true, pageable2);

        assertEquals(10, result1.getNumberOfElements());
        assertEquals("BrandName1", result1.getContent().get(0).getName());
        assertEquals("BrandName10", result1.getContent().get(9).getName());

        assertEquals(9, result2.getNumberOfElements());
        assertEquals("BrandName11", result2.getContent().get(0).getName());
        assertEquals("BrandName19", result2.getContent().get(8).getName());
    }

    @Test
    void givenInvalidId_whenGetOneById_thenThrowValidationException() {
        assertThatThrownBy(() -> brandService.getOneById(NULL_ID)).isInstanceOf(ValidationException.class).hasMessageContaining("Invalid brand ID: " + NULL_ID);
        assertThatThrownBy(() -> brandService.getOneById(NEGATIVE_ID)).isInstanceOf(ValidationException.class).hasMessageContaining("Invalid brand ID: " + NEGATIVE_ID);
    }

    @Test
    void givenNonExistentId_whenGetOneById_thenThrowNotFoundException() {
        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> brandService.getOneById(NOT_FOUND_ID)).isInstanceOf(NotFoundException.class).hasMessageContaining("Brand not found with ID: " + NOT_FOUND_ID);
    }

    @Test
    void givenExistentId_whenGetOneById_thenReturnBrand() {
        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(Brand.builder().id(VALID_ID).build()));

        var result = brandService.getOneById(VALID_ID);

        assertNotNull(result);
        assertEquals(VALID_ID, result.getId());
    }

    @Test
    void givenExistentName_whenCreate_thenThrowConflictException() {
        when(brandRepository.existsByNameAndDeleted(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(true);
        assertThatThrownBy(() -> brandService.create(brandRequestDTO)).isInstanceOf(ConflictException.class).hasMessageContaining("Brand already exists with name: " + brandRequestDTO.getName());
    }

    @Test
    void givenValidInput_whenCreate_thenReturnCreatedBrand() {
        when(brandRepository.existsByNameAndDeleted(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(false);
        when(brandRepository.save(Mockito.any(Brand.class))).thenReturn(Brand.builder().id(VALID_ID).name(NAME).build());

        var brandResponseDTO = brandService.create(brandRequestDTO);

        assertEquals(NAME, brandResponseDTO.getName());
        verify(brandRepository).save(Mockito.any(Brand.class));
    }

    @Test
    void givenInvalidId_whenUpdate_thenThrowValidationException() {
        assertThatThrownBy(() -> brandService.update(NULL_ID, new BrandRequestDTO())).isInstanceOf(ValidationException.class).hasMessageContaining("Invalid brand ID: " + NULL_ID);
        assertThatThrownBy(() -> brandService.update(NEGATIVE_ID, new BrandRequestDTO())).isInstanceOf(ValidationException.class).hasMessageContaining("Invalid brand ID: " + NEGATIVE_ID);
    }

    @Test
    void givenNonExistentId_whenUpdate_thenThrowNotFoundException() {
        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> brandService.update(NOT_FOUND_ID, new BrandRequestDTO())).isInstanceOf(NotFoundException.class).hasMessageContaining("Brand not found with ID: " + NOT_FOUND_ID);
    }

    @Test
    void givenConflictName_whenUpdate_thenThrowConflictException() {
        var nameToUpdate = "HP";
        brandRequestDTO.setName(nameToUpdate);

        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(Brand.builder().id(VALID_ID).build()));
        when(brandRepository.findByNameAndDeleted(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(Optional.of(Brand.builder().name(nameToUpdate).id(2L).build()));

        assertThatThrownBy(() -> brandService.update(VALID_ID, brandRequestDTO)).isInstanceOf(ConflictException.class).hasMessageContaining("Brand name already exists: " + nameToUpdate);
    }

    @Test
    void givenValidInputBrand_whenUpdate_thenReturnUpdatedBrand() {
        var nameToUpdate = "HP";
        brandRequestDTO.setName(nameToUpdate);

        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(Brand.builder().id(VALID_ID).build()));
        when(brandRepository.findByNameAndDeleted(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(Optional.empty());
        when(brandRepository.save(Mockito.any(Brand.class))).thenReturn(Brand.builder().id(VALID_ID).name(nameToUpdate).build());

        var updatedBrand = brandService.update(VALID_ID, brandRequestDTO);

        assertEquals(VALID_ID, updatedBrand.getId());
        assertEquals(nameToUpdate, updatedBrand.getName());
        verify(brandRepository).save(Mockito.any(Brand.class));
    }

    @Test
    void givenInvalidId_whenDelete_thenThrowValidationException() {
        assertThatThrownBy(() -> brandService.markAsDeleted(NULL_ID)).isInstanceOf(ValidationException.class).hasMessageContaining("Invalid brand ID: " + NULL_ID);
        assertThatThrownBy(() -> brandService.markAsDeleted(NEGATIVE_ID)).isInstanceOf(ValidationException.class).hasMessageContaining("Invalid brand ID: " + NEGATIVE_ID);
    }

    @Test
    void givenNonExistentId_whenDelete_thenThrowNotFoundException() {
        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> brandService.markAsDeleted(NOT_FOUND_ID)).isInstanceOf(NotFoundException.class).hasMessageContaining("Brand not found with ID: " + NOT_FOUND_ID);
    }

    @Test
    void givenBrandUsedByProducts_whenDelete_thenThrowConflictException() {
        var brand = Brand.builder().id(VALID_ID).name(NAME).build();

        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(brand));
        when(productRepository.existsByBrandAndDeleted(Mockito.any(Brand.class), Mockito.anyBoolean())).thenReturn(true);

        assertThatThrownBy(() -> brandService.markAsDeleted(VALID_ID)).isInstanceOf(ConflictException.class).hasMessageContaining("Cannot delete brand: it is used by one or more products");
    }

    @Test
    void givenBrandNotUsedByProducts_whenDelete_thenSuccess() {
        when(brandRepository.findByIdAndDeleted(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(Brand.builder().build()));
        when(productRepository.existsByBrandAndDeleted(Mockito.any(Brand.class), Mockito.anyBoolean())).thenReturn(false);
        doNothing().when(brandRepository).setDeletedTrueById(Mockito.anyLong());

        assertDoesNotThrow(() -> brandService.markAsDeleted(VALID_ID));
    }
}
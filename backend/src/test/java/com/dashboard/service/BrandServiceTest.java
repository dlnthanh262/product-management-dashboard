//package com.dashboard.service;
//
//import com.dashboard.model.Brand;
//import com.dashboard.repository.BrandRepository;
//import com.dashboard.repository.ProductRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class BrandServiceTest {
//    @Mock
//    private BrandRepository brandRepository;
//    @Mock
//    private ProductRepository productRepository;
//    private BrandService brandService;
//
//    @BeforeEach
//    void setUp() {
//        brandService = new BrandService(brandRepository, productRepository);
//    }
//
//    @Test
//    void givenTableHasRecords_whenGetAllBrands_thenReturnNonemptyList() {
//
//        List<Brand> brands = new ArrayList<>();
//        brands.add(Brand.builder().id(1L).name("Apple").build());
//        brands.add(Brand.builder().id(2L).name("HP").build());
//
//        when(brandRepository.findAll()).thenReturn(brands);
//        var results = brandService.getAll();
//
//        assertEquals(2, results.size());
//        assertEquals("Apple", results.get(0).getName());
//        assertEquals("HP", results.get(1).getName());
//    }
//
//    @Test
//    void givenEmptyTable_whenGetAllBrands_thenReturnEmptyList() {
//
//        when(brandRepository.findAll()).thenReturn(new ArrayList<>());
//        var results = brandService.getAll();
//
//        assertEquals(0, results.size());
//    }
//}
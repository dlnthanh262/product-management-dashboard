package com.dashboard.service;

import com.dashboard.model.Product;
import com.dashboard.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void givenTableHasRecords_whenGetAllProducts_thenReturnNonemptyList() {

        List<Product> products = new ArrayList<>();
        products.add(Product.builder().id(1L).name("iPhone").build());
        products.add(Product.builder().id(2L).name("Macbook").build());

        when(productRepository.findAll()).thenReturn(products);
        var results = productService.getAllProducts();

        assertEquals(2, results.size());
        assertEquals("iPhone", results.get(0).getName());
        assertEquals("Macbook", results.get(1).getName());
    }

    @Test
    void givenEmptyTable_whenGetAllProducts_thenReturnEmptyList() {

        when(productRepository.findAll()).thenReturn(new ArrayList<>());
        var results = productService.getAllProducts();

        assertEquals(0, results.size());
    }
}
package com.dashboard.config;

import com.dashboard.model.Brand;
import com.dashboard.model.Product;
import com.dashboard.repository.BrandRepository;
import com.dashboard.repository.ProductRepository;
import com.dashboard.util.CsvUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private BrandRepository brandRepository;
    @Mock
    private ProductRepository productRepository;
    private MockedStatic<CsvUtils> csvUtilsMock;
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer = new DataInitializer(productRepository, brandRepository);
        csvUtilsMock = mockStatic(CsvUtils.class);
    }

    @AfterEach
    void tearDown() {
        csvUtilsMock.close();
    }

    @Test
    void givenEmptyTables_whenRun_thenInsertAllValidRows() throws Exception {
        when(brandRepository.count()).thenReturn(0L);
        when(productRepository.count()).thenReturn(0L);

        List<String[]> mockBrandRows = Arrays.asList(
            new String[]{"Apple", "USA", "1976", "apple.com", ""},
            new String[]{"HP", "USA", "1950", "hp.com", ""}
        );
        List<String[]> mockProductRows = Arrays.asList(
            new String[]{"iPhone", "Apple", "10", "999.99"},
            new String[]{"laptop", "HP", "10", "1299.99"}
        );

        csvUtilsMock.when(() -> CsvUtils.readCsvFromResource("data/brands.csv"))
            .thenReturn(mockBrandRows);
        csvUtilsMock.when(() -> CsvUtils.readCsvFromResource("data/products.csv"))
            .thenReturn(mockProductRows);
        when(brandRepository.findByName("Apple"))
            .thenReturn(Optional.of(Brand.builder().id(1L).name("Apple").build()));
        when(brandRepository.findByName("HP"))
            .thenReturn(Optional.of(Brand.builder().id(2L).name("HP").build()));

        dataInitializer.run();

        ArgumentCaptor<List<Brand>> brandCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Product>> productCaptor = ArgumentCaptor.forClass(List.class);

        verify(brandRepository).saveAll(brandCaptor.capture());
        verify(productRepository).saveAll(productCaptor.capture());

        List<Brand> savedBrands = brandCaptor.getValue();
        assertEquals(2, savedBrands.size());

        List<Product> savedProducts = productCaptor.getValue();
        assertEquals(2, savedProducts.size());
    }

    @Test
    void givenInvalidCsvRows_whenRun_thenSkipInvalidRows() throws Exception {

        when(brandRepository.count()).thenReturn(0L);

        List<String[]> mockBrandRows = Arrays.asList(
            new String[]{"Apple", "USA", "1976", "apple.com", ""},
            new String[]{"", "", "", "", ""}
        );

        csvUtilsMock.when(() -> CsvUtils.readCsvFromResource("data/brands.csv"))
            .thenReturn(mockBrandRows);

        dataInitializer.run();

        ArgumentCaptor<List<Brand>> captor = ArgumentCaptor.forClass(List.class);
        verify(brandRepository).saveAll(captor.capture());

        List<Brand> savedBrands = captor.getValue();
        assertEquals(1, savedBrands.size());
        assertEquals("Apple", savedBrands.get(0).getName());
    }

    @Test
    void givenTablesNotEmpty_whenRun_thenSkipImport() {

        when(brandRepository.count()).thenReturn(10L);
        when(productRepository.count()).thenReturn(5L);

        dataInitializer.run();

        verify(brandRepository, never()).saveAll(any());
        verify(productRepository, never()).saveAll(any());
    }
}
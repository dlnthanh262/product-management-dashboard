package com.dashboard.parser;

import com.dashboard.model.Brand;
import com.dashboard.repository.BrandRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class ProductCsvParserTest {

    private BrandRepository brandRepository;
    private ProductCsvParser parser;

    @BeforeEach
    void setUp() {
        brandRepository = Mockito.mock(BrandRepository.class);
        parser = new ProductCsvParser(brandRepository);
    }

    @Test
    void givenValidRow_whenParse_thenReturnProduct() throws Exception {
        String[] row = { "Laptop", "Dell", "10", "999.99" };
        var brand = Brand.builder().name("Dell").build();
        when(brandRepository.findByName("Dell")).thenReturn(Optional.of(brand));

        var result = parser.parse(row);

        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getBrand()).isEqualTo(brand);
        assertThat(result.getQuantity()).isEqualTo(10);
        assertThat(result.getPrice()).isEqualTo(999.99);
    }

    @Test
    void givenRowMissingPrice_whenParse_thenThrowValidationException() {
        String[] row = { "Laptop", "Dell", "10" };

        assertThatThrownBy(() -> parser.parse(row))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid product column number");
    }

    @Test
    void givenBlankProductName_whenParse_thenThrowValidationException() {
        String[] row = { "   ", "Dell", "10", "999.99" };

        assertThatThrownBy(() -> parser.parse(row))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Product name cannot be blank");
    }

    @Test
    void givenBlankBrandName_whenParse_thenThrowValidationException() {
        String[] row = { "Laptop", "   ", "10", "999.99" };

        assertThatThrownBy(() -> parser.parse(row))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Brand name cannot be blank");
    }

    @Test
    void givenNotFoundBrand_whenParse_thenThrowValidationException() {
        String[] row = { "Laptop", "UnknownBrand", "10", "999.99" };
        when(brandRepository.findByName("UnknownBrand")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parser.parse(row))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Brand not found for name");
    }

    @Test
    void givenNonNumericQuantity_whenParse_thenThrowValidationException() {
        String[] row = { "Laptop", "Dell", "abc", "999.99" };
        when(brandRepository.findByName("Dell")).thenReturn(Optional.of(new Brand()));

        assertThatThrownBy(() -> parser.parse(row))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid quantity");
    }

    @Test
    void givenInvalidPrice_whenParse_thenThrowValidationException() {
        String[] row = { "Laptop", "Dell", "10", "priceX" };
        when(brandRepository.findByName("Dell")).thenReturn(Optional.of(new Brand()));

        assertThatThrownBy(() -> parser.parse(row))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid price");
    }
}
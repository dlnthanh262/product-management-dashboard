package com.dashboard.parser;

import com.dashboard.model.Brand;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BrandCsvParserTest {

    private BrandCsvParser parser;

    @BeforeEach
    void setUp() {
        parser = new BrandCsvParser();
    }

    @Test
    void givenValidRow_whenParse_thenReturnsCorrectBrand() throws Exception {
        String[] row = {
            "Dell", "USA", "1984", "https://www.dell.com", "Global tech company"
        };

        var result = parser.parse(row);

        assertThat(result.getName()).isEqualTo("Dell");
        assertThat(result.getCountry()).isEqualTo("USA");
        assertThat(result.getFoundedYear()).isEqualTo(1984);
        assertThat(result.getWebsite()).isEqualTo("https://www.dell.com");
        assertThat(result.getDescription()).isEqualTo("Global tech company");
    }

    @Test
    void givenRowMissingFields_whenParse_thenThrowsValidationException() {
        String[] row = { "Dell", "USA", "1984" };

        assertThatThrownBy(() -> parser.parse(row))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid brand column number");
    }

    @Test
    void givenBlankName_whenParse_thenThrowsValidationException() {
        String[] row = { "  ", "USA", "1984", "url", "desc" };

        assertThatThrownBy(() -> parser.parse(row))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Brand name cannot be blank");
    }

    @Test
    void givenNonNumericFoundedYear_whenParse_thenThrowsValidationException() {
        String[] row = { "Dell", "USA", "Nineteen84", "url", "desc" };

        assertThatThrownBy(() -> parser.parse(row))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid brand founded year");
    }

    @Test
    void givenBlankOptionalFields_whenParse_thenAssignsNull() throws Exception {
        String[] row = { "Dell", "  ", "", "  ", "   " };

        Brand result = parser.parse(row);

        assertThat(result.getName()).isEqualTo("Dell");
        assertThat(result.getCountry()).isNull();
        assertThat(result.getFoundedYear()).isNull();
        assertThat(result.getWebsite()).isNull();
        assertThat(result.getDescription()).isNull();
    }
}
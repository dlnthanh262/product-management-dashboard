package com.dashboard.util;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class CsvUtilsTest {

    @Test
    void givenValidCsv_whenReadCsvFromResource_thenReturnsRecordList() {
        String testFile = "data/brands.csv";
        List<String[]> result = CsvUtils.readCsvFromResource(testFile);

        result.forEach(row -> System.out.println(String.join(",", row)));

        assertThat(result).hasSize(4);

        assertThat(result.get(0)).containsExactly("Dell", "", "1984", "https://www.dell.com", "Dell is a global leader in personal computing and enterprise solutions, known for reliable laptops and desktops.");
        assertThat(result.get(1)).containsExactly("HP", "USA", "1939", "https://www.hp.com", "HP (Hewlett-Packard) offers a wide range of computing and printing solutions for both consumers and businesses.");
        assertThat(result.get(2)).containsExactly("Logitech", "Switzerland", "1981", "https://www.logitech.com", "Logitech is renowned for its computer peripherals, including mice, keyboards, and webcams.");
        assertThat(result.get(3)).containsExactly("Corsair", "USA", "1994", "https://www.corsair.com", "Corsair designs high-performance gaming gear and PC components for enthusiasts and eSports players.");
    }

    @Test
    void givenNonexistentCsv_whenReadCsvFromResource_thenReturnsEmptyList() {
        List<String[]> result = CsvUtils.readCsvFromResource("data/123.csv");
        assertThat(result).isEmpty();
    }
}
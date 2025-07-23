package com.dashboard.config;

import com.dashboard.model.Brand;
import com.dashboard.model.Product;
import com.dashboard.repository.BrandRepository;
import com.dashboard.repository.ProductRepository;
import com.dashboard.util.CsvUtils;
import com.dashboard.util.EntityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Override
    public void run(String... args) {
        initBrands();
        initProducts();
    }

    private void initBrands() {
        log.info("Importing brand data...");
        if (brandRepository.count() == 0) {
            List<String[]> records = CsvUtils.readCsvFromResource("data/brands.csv");
            List<Brand> brands = new ArrayList<>();

            int rowIndex = 0;
            for (String[] row : records) {
                rowIndex++;
                int numFields = EntityUtils.getNonIdFieldCount(Brand.class);
                if (row.length < numFields) {
                    log.warn("Invalid brand column number: {} instead of {} at row {}", row.length, numFields, rowIndex);
                    continue;
                }

                String name = row[0].trim();
                if (CsvUtils.isBlankOrNullString(name)) {
                    log.warn("Invalid brand name at row {}", rowIndex, Arrays.toString(row));
                    continue;
                }

                Brand brand = new Brand();
                brand.setName(name);

                String country = row[1].trim();
                brand.setCountry(CsvUtils.isBlankOrNullString(country) ? null : country);

                String foundedYearString = row[2].trim();
                if (CsvUtils.isBlankOrNullString(foundedYearString)) {
                    brand.setFounded_year(null);
                } else if (CsvUtils.isIntegerString(foundedYearString)) {
                    brand.setFounded_year(Integer.valueOf(foundedYearString));
                } else {
                    log.warn("Invalid brand founded year {} at row {}", foundedYearString, rowIndex);
                    continue;
                }

                String website = row[3].trim();
                brand.setWebsite(CsvUtils.isBlankOrNullString(website) ? null : website);

                String description = row[4].trim();
                brand.setDescription(CsvUtils.isBlankOrNullString(description) ? null : description);

                brands.add(brand);
            }

            brandRepository.saveAll(brands);
            log.info("Total brands imported: {}", brands.size());
        } else {
            log.info("Brands already exist. Skipping import.");
        }
    }

    private void initProducts() {
        log.info("Importing product data...");
        if (productRepository.count() == 0) {
            List<String[]> records = CsvUtils.readCsvFromResource("data/products.csv");
            List<Product> products = new ArrayList<>();

            int rowIndex = 0;
            for (String[] row : records) {
                rowIndex++;
                int numFields = EntityUtils.getNonIdFieldCount(Product.class);
                if (row.length < numFields) {
                    log.warn("Invalid product column number: {} instead of {} at row {}", row.length, numFields, rowIndex);
                    continue;
                }

                String name = row[0].trim();
                if (CsvUtils.isBlankOrNullString(name)) {
                    log.warn("Invalid product name at row {}: {}", rowIndex, Arrays.toString(row));
                    continue;
                }

                String brandName = row[1].trim();
                if (CsvUtils.isBlankOrNullString(brandName)) {
                    log.warn("Invalid brand name for product at row {}: {}", rowIndex, Arrays.toString(row));
                    continue;
                }
                Optional<Brand> optionalBrand = brandRepository.findByName(brandName);
                if (optionalBrand.isEmpty()) {
                    log.warn("Brand not found for name '{}' at row {}", brandName, rowIndex);
                    continue;
                }

                String quantityString = row[2].trim();
                if (!CsvUtils.isIntegerString(quantityString)) {
                    log.warn("Invalid quantity '{}' at row {}", quantityString, rowIndex);
                    continue;
                }

                String priceString = row[3].trim();
                if (!CsvUtils.isNumericString(priceString)) {
                    log.warn("Invalid price '{}' at row {}", priceString, rowIndex);
                    continue;
                }

                Product product = new Product();
                product.setName(name);
                product.setBrand(optionalBrand.get());
                product.setQuantity(Integer.parseInt(quantityString));
                product.setPrice(Double.parseDouble(priceString));

                products.add(product);
            }

            productRepository.saveAll(products);
            log.info("Total products imported: {}", products.size());
        } else {
            log.info("Products already exist. Skipping import.");
        }
    }
}

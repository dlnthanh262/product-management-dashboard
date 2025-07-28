package com.dashboard.parser;

import com.dashboard.exception.InvalidFormatException;
import com.dashboard.exception.NotFoundException;
import com.dashboard.exception.ValidationException;
import com.dashboard.model.Brand;
import com.dashboard.model.Product;
import com.dashboard.repository.BrandRepository;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class ProductCsvParser implements CsvEntityParser<Product>{

    private final BrandRepository brandRepository;

    public ProductCsvParser(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public Product parse(String[] row) {
        var expectedColumns = 4;
        if (row.length < expectedColumns) {
            throw new InvalidFormatException(String.format("Invalid product column number. Actual: %d, expected: %d", row.length, expectedColumns));
        }

        var name = row[0].trim();
        if (StringUtils.isBlank(name)) {
            throw new ValidationException("Product name cannot be blank");
        }

        String brandName = row[1].trim();
        if (StringUtils.isBlank(brandName)) {
            throw new ValidationException("Brand name cannot be blank");
        }

        Optional<Brand> optionalBrand = brandRepository.findByName(brandName);
        if (optionalBrand.isEmpty()) {
            throw new NotFoundException(String.format("Brand not found for name '%s'", brandName));
        }

        var quantityString = row[2].trim();
        if (!StringUtils.isNumeric(quantityString)) {
            throw new InvalidFormatException(String.format("Invalid quantity '%s'", quantityString));
        }
        var quantity = Integer.parseInt(quantityString);

        var priceString = row[3].trim();
        double price;
        try {
            price = Double.parseDouble(priceString);
        } catch (NumberFormatException e) {
            throw new InvalidFormatException(String.format("Invalid price '%s'", priceString));
        }

        return Product.builder()
            .name(name)
            .brand(optionalBrand.get())
            .quantity(quantity)
            .price(price)
            .build();
    }
}

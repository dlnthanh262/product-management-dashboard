package com.dashboard.parser;

import com.dashboard.model.Brand;
import jakarta.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;

public class BrandCsvParser implements CsvEntityParser<Brand> {
    @Override
    public Brand parse(String[] row) throws Exception {
        var expectedColumns = 5;
        if (row.length < expectedColumns) {
            throw new ValidationException(String.format("Invalid brand column number: %d instead of %d", row.length, expectedColumns));
        }

        var name = row[0].trim();
        if (StringUtils.isBlank(name)) {
            throw new ValidationException("Brand name cannot be blank");
        }

        var foundedYearString = row[2].trim();
        if (StringUtils.isBlank(foundedYearString)) {
            foundedYearString = null;
        } else if (!StringUtils.isNumeric(foundedYearString)) {
            throw new ValidationException(String.format("Invalid brand founded year %s", foundedYearString));
        }
        var foundedYear = foundedYearString == null ? null : Integer.parseInt(foundedYearString);

        var country = row[1].trim();
        country = StringUtils.isBlank(country) ? null : country;

        var website = row[3].trim();
        website = StringUtils.isBlank(website) ? null : website;

        var description = row[4].trim();
        description = StringUtils.isBlank(description) ? null : description;

        return Brand.builder()
            .name(name)
            .country(country)
            .foundedYear(foundedYear)
            .website(website)
            .description(description)
            .build();
    }
}
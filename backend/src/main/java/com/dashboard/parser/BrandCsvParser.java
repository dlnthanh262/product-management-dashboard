package com.dashboard.parser;

import com.dashboard.exception.InvalidFormatException;
import com.dashboard.exception.ValidationException;
import com.dashboard.model.Brand;
import org.apache.commons.lang3.StringUtils;

public class BrandCsvParser implements CsvEntityParser<Brand> {
    @Override
    public Brand parse(String[] row) {
        var expectedColumns = 5;
        if (row.length < expectedColumns) {
            throw new InvalidFormatException(String.format("Invalid brand column number. Actual: %d, expected: %d", row.length, expectedColumns));
        }

        var name = row[0].trim();
        if (StringUtils.isBlank(name)) {
            throw new ValidationException("Brand name cannot be blank");
        }

        var foundedYearString = row[2].trim();
        Integer foundedYear = null;
        if (StringUtils.isNotBlank(foundedYearString)) {
            if (!StringUtils.isNumeric(foundedYearString)) {
                throw new InvalidFormatException(String.format("Invalid brand founded year '%s'", foundedYearString));
            }
            foundedYear = Integer.parseInt(foundedYearString);
        }

        var country = StringUtils.defaultIfBlank(row[1].trim(), null);
        var website = StringUtils.defaultIfBlank(row[3].trim(), null);
        var description = StringUtils.defaultIfBlank(row[4].trim(), null);

        return Brand.builder()
            .name(name)
            .country(country)
            .foundedYear(foundedYear)
            .website(website)
            .description(description)
            .build();
    }
}
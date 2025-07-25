package com.dashboard.parser;

import com.dashboard.model.Brand;
import com.dashboard.util.EntityUtils;
import org.apache.commons.lang3.StringUtils;

public class BrandCsvParser implements CsvEntityParser<Brand> {
    @Override
    public Brand parse(String[] row) throws Exception {
        var numFields = EntityUtils.getNonIdFieldCount(Brand.class);
        if (row.length < numFields) {
            throw new Exception(String.format("Invalid brand column number: %d instead of %d", row.length, numFields));
        }

        var name = row[0].trim();
        if (StringUtils.isBlank(name)) {
            throw new Exception(String.format("Invalid brand name: %s", name));
        }

        var foundedYearString = row[2].trim();
        if (StringUtils.isBlank(foundedYearString)) {
            foundedYearString = null;
        } else if (!StringUtils.isNumeric(foundedYearString)) {
            throw new Exception(String.format("Invalid brand founded year %s", foundedYearString));
        }
        var foundedYear = foundedYearString == null ? null : Integer.parseInt(foundedYearString);

        var country = row[1].trim();
        country = StringUtils.isBlank(country) ? null : country;

        var website = row[3].trim();
        website = StringUtils.isBlank(website) ? null : website;

        var description = row[4].trim();
        description = StringUtils.isBlank(description) ? null : description;

        var brand = new Brand();
        brand.setName(name);
        brand.setCountry(country);
        brand.setFounded_year(foundedYear);
        brand.setWebsite(website);
        brand.setDescription(description);

        return brand;
    }
}
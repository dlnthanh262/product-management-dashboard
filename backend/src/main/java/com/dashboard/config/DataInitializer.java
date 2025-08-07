package com.dashboard.config;

import com.dashboard.parser.BrandCsvParser;
import com.dashboard.parser.CsvEntityParser;
import com.dashboard.parser.ProductCsvParser;
import com.dashboard.parser.UsersCsvParser;
import com.dashboard.repository.BrandRepository;
import com.dashboard.repository.ProductRepository;
import com.dashboard.repository.UsersRepository;
import com.dashboard.util.CsvUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final UsersRepository usersRepository;
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Override
    public void run(String... args) {
        var brandCsvParser = new BrandCsvParser();
        var productCsvParser = new ProductCsvParser(brandRepository);
        var userCsvParser = new UsersCsvParser(usersRepository);
        productRepository.deleteAll();
        brandRepository.deleteAll();
        usersRepository.deleteAll();
        insertRecordsFromCsvString("brand", "brands.csv", brandCsvParser, brandRepository);
        insertRecordsFromCsvString("product", "products.csv", productCsvParser, productRepository);
        insertRecordsFromCsvString("users", "users.csv", userCsvParser, usersRepository);
    }

    private <T, ID> void insertRecordsFromCsvString(
        String tableName,
        String fileName,
        CsvEntityParser<T> parser,
        JpaRepository<T, ID> repository
    ) {
        log.info("Importing {} data...", tableName);
        if (repository.count() == 0) {
            List<String[]> records = CsvUtils.readCsvFromResource(String.format("data/%s", fileName));
            List<T> entities = new ArrayList<>();
            var skipCount = 0;

            for (int i = 0; i < records.size(); i++) {
                try {
                    T entity = parser.parse(records.get(i));
                    entities.add(entity);
                } catch (Exception exception) {
                    log.warn("Can not import row {}. Error message: {}", i + 1, exception.getMessage());
                    skipCount++;
                }
            }

            repository.saveAll(entities);
            log.info("Finished importing {}. Total: {}, Skipped: {}", tableName, entities.size(), skipCount);
        } else {
            log.info("{} data already exist. Skipping import.", tableName);
        }
    }
}
package com.dashboard.config;

import com.dashboard.model.Product;
import com.dashboard.repository.ProductRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Autowired
    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("data/products.csv");
            if (inputStream == null) {
                System.out.println("products.csv not found in resources/data/");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

                List<String[]> records = csvReader.readAll();
                List<Product> populations = new ArrayList<>();

                for (String[] row : records) {
                    Product product = new Product();
                    product.setName(row[0]);
                    product.setBrand(row[1]);
                    product.setQuantity(Integer.parseInt(row[2]));
                    product.setPrice(Double.parseDouble(row[3]));
                    populations.add(product);
                }

                productRepository.saveAll(populations);
                System.out.println("products.csv imported successfully.");
            } catch (IOException | CsvException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Data already exists. Skipping import.");
        }
    }
}

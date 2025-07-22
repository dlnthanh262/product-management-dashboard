package com.dashboard.config;

import com.dashboard.model.Product;
import com.dashboard.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final ProductRepository productRepository;

    @Autowired
    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void init() {
        if (productRepository.count() == 0) {
            Product p1 = new Product("Laptop", 3, 10000000.0);
            Product p2 = new Product("Mouse", 3, 10000000.0);
            Product p3 = new Product("Keyboard", 3, 10000000.0);

            productRepository.save(p1);
            productRepository.save(p2);
            productRepository.save(p3);
        }
    }
}

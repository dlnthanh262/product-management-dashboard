package com.dashboard.controller;

import com.dashboard.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class ProductController {

    @GetMapping("/api/products")
    public List<Product> getProducts() {
        return List.of(
            new Product(1, "Laptop", 5, 1000000),
            new Product(2, "Mouse", 12, 200000),
            new Product(3, "Keyboard", 7, 500000)
        );
    }
}
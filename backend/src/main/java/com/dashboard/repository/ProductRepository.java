package com.dashboard.repository;

import com.dashboard.model.Brand;
import com.dashboard.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByBrand(Brand brand);
}
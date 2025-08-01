package com.dashboard.repository;

import com.dashboard.model.Brand;
import com.dashboard.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByBrandAndDeleted(Brand brand, Boolean deleted);
    boolean existsByNameAndBrand(String name, Brand brand);
    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.deleted = true WHERE p.id = :id")
    void setDeletedTrueById(Long id);
}
package com.dashboard.repository;

import com.dashboard.model.Brand;
import com.dashboard.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByDeleted(boolean deleted, Pageable pageable);
    Optional<Product> findByIdAndDeleted(Long id, Boolean deleted);
    Optional<Product> findByBrandAndNameAndDeletedFalse(Brand brand, String name);
    boolean existsByBrandAndDeleted(Brand brand, Boolean deleted);
    boolean existsByNameAndBrandAndDeletedFalse(String name, Brand brand);

    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.deleted = true WHERE p.id = :id")
    void setDeletedTrueById(Long id);

    @Query("""
    SELECT p FROM Product p
    WHERE p.deleted = :deleted
    AND (:name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
    AND (:brand = '' OR LOWER(p.brand.name) = LOWER(:brand))
    AND (:minPrice IS NULL OR p.price >= :minPrice)
    AND (:maxPrice IS NULL OR p.price <= :maxPrice)
    """)
    Page<Product> searchProducts(
        @Param("deleted") boolean deleted,
        @Param("name") String name,
        @Param("brand") String brand,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        Pageable pageable
    );

    @Query("SELECT p.brand.name, COUNT(p) FROM Product p GROUP BY p.brand.name")
    List<Object[]> countProductsGroupedByBrand();
}
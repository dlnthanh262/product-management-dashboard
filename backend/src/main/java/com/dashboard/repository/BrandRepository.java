package com.dashboard.repository;

import com.dashboard.model.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Page<Brand> findByDeleted(boolean deleted, Pageable pageable);
    Optional<Brand> findByIdAndDeleted(Long id, Boolean deleted);
    Optional<Brand> findByNameAndDeleted(String name, Boolean deleted);
    boolean existsByNameAndDeleted(String name, Boolean deleted);
    @Transactional
    @Modifying
    @Query("UPDATE Brand b SET b.deleted = true WHERE b.id = :id")
    void setDeletedTrueById(Long id);
}
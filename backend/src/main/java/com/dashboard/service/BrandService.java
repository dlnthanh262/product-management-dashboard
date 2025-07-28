package com.dashboard.service;

import com.dashboard.dto.BrandDTO;
import com.dashboard.exception.ConflictException;
import com.dashboard.exception.NotFoundException;
import com.dashboard.model.Brand;
import com.dashboard.repository.BrandRepository;
import com.dashboard.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand createBrand(BrandDTO brandDTO) {
        var optionalBrand = brandRepository.findByName(brandDTO.getName());
        if (optionalBrand.isPresent()) {
            throw new ConflictException("Brand already exists with name: " + brandDTO.getName());
        }

        var newBrand = Brand.builder()
            .name(brandDTO.getName())
            .country(brandDTO.getCountry())
            .foundedYear(brandDTO.getFoundedYear())
            .website(brandDTO.getWebsite())
            .description(brandDTO.getDescription())
            .build();

        return brandRepository.save(newBrand);
    }

//    public Brand updateBrand(Long id, Brand brandDetails) {
//        var optionalBrand = brandRepository.findById(id);
//        if (optionalBrand.isEmpty()) {
//            throw new NotFoundException("Brand not found");
//        }
//
//        var brand = optionalBrand.get();
//
//        brand.setName(brandDetails.getName());
//        brand.setCountry(brandDetails.getCountry());
//        brand.setFoundedYear(brandDetails.getFoundedYear());
//        brand.setWebsite(brandDetails.getWebsite());
//        brand.setDescription(brandDetails.getDescription());
//
//        return brandRepository.save(brand);
//    }
//
//    public void deleteBrand(Long id) {
//        var optionalBrand = brandRepository.findById(id);
//        if (optionalBrand.isEmpty()) {
//            throw new NotFoundException("Brand not found");
//        }
//
//        var brand = optionalBrand.get();
//
//        boolean hasProducts = productRepository.existsByBrand(brand);
//        if (hasProducts) {
//            throw new ConflictException("Cannot delete brand: it is used by one or more products");
//        }
//
//        brandRepository.deleteById(id);
//    }
}

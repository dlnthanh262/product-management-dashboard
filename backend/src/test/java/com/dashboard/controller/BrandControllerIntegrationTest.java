package com.dashboard.controller;

import com.dashboard.model.Brand;
import com.dashboard.repository.BrandRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BrandControllerIntegrationTest {

    private static final String BRAND_NAME = "Test Brand";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BrandRepository brandRepository;

    @BeforeEach
    void setUp() {
        brandRepository.deleteAll();
    }

    @Test
    void givenValidRequest_whenGetAll_thenReturnListOfBrands() throws Exception {
        var brand = Brand.builder().name(BRAND_NAME).deleted(false).build();
        brandRepository.save(brand);

        mockMvc.perform(get("/api/brands"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].name").value(BRAND_NAME));
    }

    @Test
    void givenValidRequest_whenGetById_thenReturnBrand() throws Exception {
        var brand = brandRepository.save(Brand.builder().name(BRAND_NAME).deleted(false).build());
        var id = brand.getId();

        mockMvc.perform(get("/api/brands/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(BRAND_NAME));
    }

    @Test
    void givenValidRequest_whenCreate_thenReturnCreatedBrand() throws Exception {
        String country = "USA";
        int foundedYear = 2000;
        String website = "https://example.com";
        String description = "A test brand";

        String json = String.format("""
        {
          "name": "%s",
          "country": "%s",
          "foundedYear": %d,
          "website": "%s",
          "description": "%s"
        }
        """, BRAND_NAME, country, foundedYear, website, description);

        mockMvc.perform(post("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value(BRAND_NAME));
    }

    @Test
    public void givenValidRequest_whenUpdate_thenReturnUpdatedBrand() throws Exception {
        var brand = brandRepository.save(Brand.builder().name(BRAND_NAME).deleted(false).build());

        var nameToUpdate = "Brand To Update";
        String country = "USA";
        int foundedYear = 2000;
        String website = "https://example.com";
        String description = "A test brand";

        String json = String.format("""
        {
          "name": "%s",
          "country": "%s",
          "foundedYear": %d,
          "website": "%s",
          "description": "%s"
        }
        """, nameToUpdate, country, foundedYear, website, description);

        mockMvc.perform(put("/api/brands/" + brand.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(nameToUpdate));
    }

    @Test
    public void givenValidRequest_whenDelete_thenDeleteSuccess() throws Exception {
        var brand = brandRepository.save(Brand.builder().name(BRAND_NAME).deleted(false).build());

        mockMvc.perform(delete("/api/brands/" + brand.getId()))
            .andExpect(status().isNoContent());

        Assertions.assertTrue(brandRepository.findById(brand.getId()).get().getDeleted());
    }

    @Test
    public void givenInvalidRequest_whenCreate_thenReturnErrorReasons() throws Exception {
        String json = """
            {
              "name": "",
              "country": "USA",
              "foundedYear": 9000,
              "website": "abc",
              "description": "test"
            }
        """;

        mockMvc.perform(post("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message.*", hasSize(3)));
    }
}
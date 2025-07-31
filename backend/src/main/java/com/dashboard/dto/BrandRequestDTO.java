package com.dashboard.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandRequestDTO {

    @NotBlank(message = "Brand name is required")
    @Size(max = 255, message = "Brand name must not exceed 255 characters")
    private String name;

    private String country;

    @Min(value = 1000, message = "Founded year must be greater than 1000 and smaller than 2100")
    @Max(value = 2100, message = "Founded year must be greater than 1000 and smaller than 2100")
    private Integer foundedYear;

    @Pattern(regexp = "^(https?://)?[\\w.-]+(?:\\.[\\w\\.-]+)+[/#?]?.*$", message = "Invalid website URL")
    private String website;

    private String description;
}


package com.dashboard.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "brand")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Brand name is required")
    @Size(max = 255, message = "Brand name must not exceed 255 characters")
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "country")
    private String country;

    @Min(value = 1000, message = "Founded year must be greater than 1000 and smaller than 2100")
    @Max(value = 2100, message = "Founded year must be greater than 1000 and smaller than 2100")
    @Column(name = "founded_year")
    private Integer foundedYear;

    @Pattern(regexp = "^(https?://)?[\\w.-]+(?:\\.[\\w\\.-]+)+[/#?]?.*$", message = "Invalid website URL")
    @Column(name = "website")
    private String website;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
package com.gdldv.reservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "discount_rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la règle est obligatoire")
    @Column(nullable = false, unique = true)
    private String ruleName;

    @NotNull(message = "Le nombre minimum de locations complétées est obligatoire")
    @Min(value = 1, message = "Le nombre minimum de locations doit être au moins 1")
    @Column(nullable = false)
    private Integer minCompletedRentals;

    @NotNull(message = "Le pourcentage de réduction est obligatoire")
    @Min(value = 1, message = "Le pourcentage de réduction doit être au moins 1")
    @Column(nullable = false)
    private Integer discountPercentage;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

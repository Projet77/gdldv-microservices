package com.gdldv.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequest {

    @NotNull(message = "La date de début est obligatoire")
    private LocalDateTime startDate;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime endDate;

    private String category;      // SUV, Sedan, Compact, etc.
    private Double minPrice;
    private Double maxPrice;

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 10;
}

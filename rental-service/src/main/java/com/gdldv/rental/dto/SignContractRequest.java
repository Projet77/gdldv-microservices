package com.gdldv.rental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignContractRequest {

    @NotNull(message = "L'ID du contrat est obligatoire")
    private Long contractId;

    @NotBlank(message = "La signature est obligatoire")
    private String signature; // Base64 de la signature électronique

    private Boolean acceptTerms = false;
}

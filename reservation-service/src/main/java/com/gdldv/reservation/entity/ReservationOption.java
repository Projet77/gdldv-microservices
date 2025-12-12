package com.gdldv.reservation.entity;

import lombok.*;
import jakarta.persistence.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationOption {
    private String optionName;   // GPS, Siège bébé, Assurance
    private Double optionPrice;  // Prix de l'option
    private Integer quantity;    // Quantité (généralement 1)
}
